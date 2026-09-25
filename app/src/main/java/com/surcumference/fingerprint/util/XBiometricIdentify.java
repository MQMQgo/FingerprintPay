package com.surcumference.fingerprint.util;

import android.content.Context;
import android.os.Build;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.surcumference.fingerprint.Constant;
import com.surcumference.fingerprint.plugin.inf.IMockCurrentUser;
import com.surcumference.fingerprint.util.log.L;
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint;
import com.wei.android.lib.fingerprintidentify.bean.FingerprintIdentifyFailInfo;
import com.wei.android.lib.fingerprintidentify.util.CryptoObjectHelper;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;

/**
 * Hardened biometric identify:
 * - the payment password is only ever encrypted / decrypted with an AES-256-GCM key that lives in the
 *   hardware-backed Android Keystore and requires BIOMETRIC_STRONG authentication for every use;
 * - storage format: Base64(IV[12] || ciphertext || tag[16]);
 * - no software fallback: every failure is reported through {@link IdentifyListener#onFailed} so the
 *   caller can restore the host app's native password input.
 */
public class XBiometricIdentify<T extends XBiometricIdentify>{

    private final Context context;
    private final FingerprintIdentify fingerprintIdentify;

    private IMockCurrentUser mockCurrentUserCallback;
    public boolean fingerprintScanStateReady = false;

    private int cipherMode = -1;
    /** plain text (encrypt mode only), dropped right after use */
    private String plainText;
    /** ciphertext+tag (decrypt mode only) */
    private byte[] cipherBytes;
    private boolean invalidBlob;

    public XBiometricIdentify(Context context) {
        this.context = context;

        fingerprintIdentify = new FingerprintIdentify(context);
        fingerprintIdentify.setMaxAvailableTimes(8);
        fingerprintIdentify.setExceptionListener(exception -> L.e("XBiometricIdentify", exception));
    }

    public T withMockCurrentUserCallback(IMockCurrentUser mockCurrentUserCallback) {
        this.mockCurrentUserCallback = mockCurrentUserCallback;
        return (T)this;
    }

    public T withEncryptionMode(@NonNull String plainText, @NonNull String keyAlias) {
        this.cipherMode = Cipher.ENCRYPT_MODE;
        this.plainText = plainText;
        this.cipherBytes = null;
        this.invalidBlob = false;
        fingerprintIdentify.setKeyAlias(keyAlias);
        fingerprintIdentify.setCipherMode(Cipher.ENCRYPT_MODE, null);
        return (T)this;
    }

    public T withDecryptionMode(@Nullable String blobBase64, @Nullable String keyAlias) {
        this.cipherMode = Cipher.DECRYPT_MODE;
        this.plainText = null;
        this.cipherBytes = null;
        this.invalidBlob = true;
        byte[] iv = null;
        try {
            if (blobBase64 != null && keyAlias != null) {
                byte[] blob = Base64.decode(blobBase64, Base64.NO_WRAP);
                // IV + at least the 16 byte GCM tag
                if (blob.length > CryptoObjectHelper.GCM_IV_LENGTH + CryptoObjectHelper.GCM_TAG_BITS / 8) {
                    iv = Arrays.copyOfRange(blob, 0, CryptoObjectHelper.GCM_IV_LENGTH);
                    this.cipherBytes = Arrays.copyOfRange(blob, CryptoObjectHelper.GCM_IV_LENGTH, blob.length);
                    this.invalidBlob = false;
                }
            }
        } catch (Exception e) {
            L.e("XBiometricIdentify: invalid stored blob", e.getClass().getName());
        }
        fingerprintIdentify.setKeyAlias(keyAlias);
        fingerprintIdentify.setCipherMode(Cipher.DECRYPT_MODE, iv);
        return (T)this;
    }

    public T startIdentify(IdentifyListener identifyListener) {
        XBiometricIdentifyManager xBiometricIdentifyManager =  XBiometricIdentifyManager.INSTANCE;
        try {
            xBiometricIdentifyManager.cancelFingerprintIdentify();
            xBiometricIdentifyManager.set(this);
            final int cipherMode = this.cipherMode;
            if (cipherMode != Cipher.ENCRYPT_MODE && cipherMode != Cipher.DECRYPT_MODE) {
                throw new IllegalStateException("Encrypt mode or decrypt mode not set");
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                // No auth-bound Keystore keys before Android 6.0
                xBiometricIdentifyManager.set(null);
                onNotify(NotifyEnum.OnBiometricNotSupported);
                identifyListener.onFailed(XBiometricIdentify.this, new FingerprintIdentifyFailInfo(false, -1, "unsupported api level"));
                return (T)this;
            }
            if (cipherMode == Cipher.DECRYPT_MODE && invalidBlob) {
                xBiometricIdentifyManager.set(null);
                FingerprintIdentifyFailInfo failInfo = new FingerprintIdentifyFailInfo(false, -1, "invalid stored password");
                failInfo.keyInvalidated = true;
                onNotify(NotifyEnum.OnKeyInvalidated);
                identifyListener.onFailed(XBiometricIdentify.this, failInfo);
                return (T)this;
            }
            callMockCurrentUserCallback(true);
            fingerprintIdentify.init();
            if (!fingerprintIdentify.isFingerprintEnable()) {
                fingerprintScanStateReady = false;
                callMockCurrentUserCallback(false);
                xBiometricIdentifyManager.set(null);
                if (Constant.PACKAGE_NAME_QQ.equals(context.getPackageName())
                        && !PermissionUtils.hasFingerprintPermission(context)) {
                    L.d("QQ 版本过低");
                    onNotify(NotifyEnum.OnQQVersionTooLow);
                } else {
                    L.d("系统指纹功能未启用");
                    onNotify(NotifyEnum.OnBiometricNotEnable);
                }
                identifyListener.onFailed(XBiometricIdentify.this, new FingerprintIdentifyFailInfo(false, -1, "biometric not enabled"));
                return (T)this;
            }
            identifyListener.onInited(XBiometricIdentify.this);
            fingerprintScanStateReady = true;
            fingerprintIdentify.startIdentify(new BaseFingerprint.IdentifyListener() {
                @Override
                public void onSucceed(@Nullable Cipher cipher) {
                    xBiometricIdentifyManager.set(null);
                    fingerprintScanStateReady = false;
                    try {
                        if (cipher == null) {
                            throw new IllegalStateException("authenticated cipher missing");
                        }
                        if (cipherMode == Cipher.ENCRYPT_MODE) {
                            String plain = plainText;
                            plainText = null;
                            if (plain == null) {
                                throw new IllegalStateException("nothing to encrypt");
                            }
                            byte[] ct = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
                            byte[] iv = cipher.getIV();
                            if (iv == null || iv.length != CryptoObjectHelper.GCM_IV_LENGTH) {
                                throw new IllegalStateException("unexpected GCM IV length");
                            }
                            byte[] blob = new byte[iv.length + ct.length];
                            System.arraycopy(iv, 0, blob, 0, iv.length);
                            System.arraycopy(ct, 0, blob, iv.length, ct.length);
                            identifyListener.onEncryptionSuccess(XBiometricIdentify.this, Base64.encodeToString(blob, Base64.NO_WRAP), iv);
                        } else {
                            byte[] pt = cipher.doFinal(cipherBytes);
                            String decrypted = new String(pt, StandardCharsets.UTF_8);
                            Arrays.fill(pt, (byte) 0);
                            identifyListener.onDecryptionSuccess(XBiometricIdentify.this, decrypted);
                        }
                    } catch (Throwable t) {
                        // never log plaintext/ciphertext, only the exception type
                        L.e("XBiometricIdentify: cipher operation failed", t.getClass().getName());
                        FingerprintIdentifyFailInfo failInfo = new FingerprintIdentifyFailInfo(false, t);
                        if (cipherMode == Cipher.DECRYPT_MODE && t instanceof AEADBadTagException) {
                            // ciphertext does not belong to this key any more: discard it
                            failInfo.keyInvalidated = true;
                            onNotify(NotifyEnum.OnKeyInvalidated);
                        } else {
                            onNotify(cipherMode == Cipher.DECRYPT_MODE ? NotifyEnum.OnDecryptionFailed : NotifyEnum.OnEncryptionFailed);
                        }
                        identifyListener.onFailed(XBiometricIdentify.this, failInfo);
                    } finally {
                        callMockCurrentUserCallback(false);
                    }
                }

                @Override
                public void onNotMatch(int availableTimes) {
                    L.d("指纹识别失败，还可尝试" + String.valueOf(availableTimes) + "次");
                    onNotify(NotifyEnum.OnBiometricNotMatch);
                    callMockCurrentUserCallback(false);
                    identifyListener.onNotMatch(XBiometricIdentify.this, availableTimes);
                }

                @Override
                public void onFailed(FingerprintIdentifyFailInfo failInfo) {
                    try {
                        xBiometricIdentifyManager.set(null);
                        plainText = null;
                        if (failInfo.keyInvalidated) {
                            onNotify(NotifyEnum.OnKeyInvalidated);
                        } else if (!failInfo.isCancel()) {
                            if (fingerprintScanStateReady) {
                                onNotify(NotifyEnum.OnBiometricRetryEnded, failInfo.errString == null ? "" : failInfo.errString);
                            }
                            L.d("指纹验证失败", failInfo);
                        }
                        fingerprintScanStateReady = false;
                        identifyListener.onFailed(XBiometricIdentify.this, failInfo);
                    } finally {
                        callMockCurrentUserCallback(false);
                    }
                }

                @Override
                public void onStartFailedByDeviceLocked() {
                    try {
                        xBiometricIdentifyManager.set(null);
                        fingerprintScanStateReady = false;
                        // 第一次调用startIdentify失败，因为设备被暂时锁定
                        L.d("系统限制，重启后必须验证密码后才能使用指纹验证");
                        onNotify(NotifyEnum.OnBiometricLocked);
                        identifyListener.onFailed(XBiometricIdentify.this, new FingerprintIdentifyFailInfo(true));
                    } finally {
                        callMockCurrentUserCallback(false);
                    }
                }
            });
        } catch (Throwable t) {
            L.e("XBiometricIdentify: startIdentify failed", t.getClass().getName());
            fingerprintScanStateReady = false;
            plainText = null;
            xBiometricIdentifyManager.set(null);
            callMockCurrentUserCallback(false);
            identifyListener.onFailed(XBiometricIdentify.this, new FingerprintIdentifyFailInfo(false, t));
        }
        return (T)this;
    }

    public void cancelIdentify() {
        fingerprintScanStateReady = false;
        fingerprintIdentify.cancelIdentify();
        callMockCurrentUserCallback(false);
    }

    public void resumeIdentify() {
        callMockCurrentUserCallback(true);
        fingerprintIdentify.resumeIdentify();
        fingerprintScanStateReady = fingerprintIdentify.isFingerprintEnable();
    }

    public boolean isUsingBiometricApi() {
        return fingerprintIdentify.isUsingBiometricApi();
    }

    private void callMockCurrentUserCallback(boolean mock) {
        IMockCurrentUser mockCurrentUserCallback = this.mockCurrentUserCallback;
        if (mockCurrentUserCallback == null) {
            return;
        }
        mockCurrentUserCallback.setMockCurrentUser(mock);
    }

    protected void onNotify(NotifyEnum notifyEnum, Object...args) {

    }

    public static class IdentifyListener<T extends XBiometricIdentify> {

        private IdentifyListener<T> parentIdentifyListener;

        public IdentifyListener() {
            this(null);
        }

        public IdentifyListener(IdentifyListener<T> parentIdentifyListener) {
            this.parentIdentifyListener = parentIdentifyListener;
        }

        public void onInited(T identify) {
            IdentifyListener<T> listener = this.parentIdentifyListener;
            if (listener != null) {
                listener.onInited(identify);
            }
        }

        public void onDecryptionSuccess(T identify, @NonNull String decryptedContent) {
            IdentifyListener<T> listener = this.parentIdentifyListener;
            if (listener != null) {
                listener.onDecryptionSuccess(identify, decryptedContent);
            }
        }
        
        public void onEncryptionSuccess(T identify, @NonNull String encryptedContent, @Nullable byte[] encryptedIV) {
            IdentifyListener<T> listener = this.parentIdentifyListener;
            if (listener != null) {
                listener.onEncryptionSuccess(identify, encryptedContent, encryptedIV);
            }
        }

        public void onNotMatch(T identify, int availableTimes) {
            IdentifyListener<T> listener = this.parentIdentifyListener;
            if (listener != null) {
                listener.onNotMatch(identify, availableTimes);
            }
        }

        public void onFailed(T identify, FingerprintIdentifyFailInfo failInfo) {
            IdentifyListener<T> listener = this.parentIdentifyListener;
            if (listener != null) {
                listener.onFailed(identify, failInfo);
            }
        }

        public void onStartFailedByDeviceLocked(T identify) {
            IdentifyListener<T> listener = this.parentIdentifyListener;
            if (listener != null) {
                listener.onStartFailedByDeviceLocked(identify);
            }
        }
    }

    public enum NotifyEnum {
        OnBiometricNotEnable,
        OnQQVersionTooLow,
        OnDecryptionFailed,
        OnBiometricRetryEnded,
        OnBiometricLocked,
        OnBiometricNotMatch,
        OnBiometricNotSupported,
        OnKeyInvalidated,
        OnEncryptionFailed,
    }

}
