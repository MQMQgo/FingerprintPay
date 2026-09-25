// Modified by mqmqgo, 2026-09-25: AES-256-GCM Keystore-only encrypt/decrypt, no software fallback, char[] password
package com.surcumference.fingerprint.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hjq.toast.Toaster;
import com.surcumference.fingerprint.Lang;
import com.surcumference.fingerprint.R;
import com.surcumference.fingerprint.util.log.L;

import com.wei.android.lib.fingerprintidentify.bean.FingerprintIdentifyFailInfo;
import com.wei.android.lib.fingerprintidentify.util.CryptoObjectHelper;

public class BizBiometricIdentify extends XBiometricIdentify<BizBiometricIdentify> {

    private final Context context;
    public BizBiometricIdentify(Context context) {
        super(context);
        this.context = context;
    }

    public BizBiometricIdentify decryptPasscode(String passcodeEncrypted, IdentifyListener identifyListener) {
        Config config = Config.from(context);
        withDecryptionMode(passcodeEncrypted, config.getPasswordKeyAlias());
        startIdentify(new IdentifyListener(identifyListener) {
            @Override
            public void onFailed(BizBiometricIdentify identify, FingerprintIdentifyFailInfo failInfo) {
                if (failInfo != null && failInfo.keyInvalidated) {
                    // key permanently invalidated (new enrollment / lock screen removed) or blob unusable:
                    // delete key + stored ciphertext, user has to set the password again.
                    try {
                        config.clearPassword();
                    } catch (Throwable t) {
                        L.e("BizBiometricIdentify: clearPassword failed", t.getClass().getName());
                    }
                }
                super.onFailed(identify, failInfo);
            }
        });
        return this;
    }

    /** @param passcode password chars; copied internally, the caller must wipe its own array */
    public BizBiometricIdentify encryptPasscode(char[] passcode, IdentifyListener identifyListener) {
        Config config = Config.from(context);
        final String newAlias = config.getNextPasswordKeyAlias();
        withEncryptionMode(passcode, newAlias);
        startIdentify(new IdentifyListener(identifyListener) {
            @Override
            public void onEncryptionSuccess(BizBiometricIdentify identify, @NonNull String encryptedContent, @Nullable byte[] encryptedIV) {
                String oldAlias = config.getPasswordKeyAlias();
                config.setPasswordEncrypted(encryptedContent, newAlias);
                config.commit();
                if (oldAlias != null && !oldAlias.equals(newAlias)) {
                    CryptoObjectHelper.removeKey(oldAlias);
                }
                super.onEncryptionSuccess(identify, encryptedContent, encryptedIV);
            }

            @Override
            public void onFailed(BizBiometricIdentify identify, FingerprintIdentifyFailInfo failInfo) {
                // the freshly generated key of the unused slot is useless, the stored password (if any) stays valid
                if (!newAlias.equals(config.getPasswordKeyAlias())) {
                    CryptoObjectHelper.removeKey(newAlias);
                }
                super.onFailed(identify, failInfo);
            }
        });
        return this;
    }

    @Override
    protected void onNotify(NotifyEnum notifyEnum, Object... args) {
        super.onNotify(notifyEnum, args);
        switch (notifyEnum) {
            case OnBiometricNotEnable:
                NotifyUtils.notifyBiometricIdentify(this.context, Lang.getString(R.id.toast_fingerprint_not_enable));
                break;
            case OnQQVersionTooLow:
                Toaster.showLong(Lang.getString(R.id.toast_need_qq_7_2_5));
                break;
            case OnDecryptionFailed:
                NotifyUtils.notifyBiometricIdentify(this.context, Lang.getString(R.id.toast_fingerprint_password_dec_failed));
                break;
            case OnBiometricRetryEnded:
                NotifyUtils.notifyBiometricIdentify(this.context, Lang.getString(R.id.toast_fingerprint_retry_ended) + " " + args[0]);
                break;
            case OnBiometricLocked:
                NotifyUtils.notifyBiometricIdentify(this.context, Lang.getString(R.id.toast_fingerprint_unlock_reboot));
                break;
            case OnBiometricNotMatch:
                NotifyUtils.notifyBiometricIdentify(this.context, Lang.getString(R.id.toast_fingerprint_not_match));
                break;
            case OnBiometricNotSupported:
                NotifyUtils.notifyBiometricIdentify(this.context, Lang.getString(R.id.toast_fingerprint_not_supported));
                break;
            case OnKeyInvalidated:
                Toaster.showLong(Lang.getString(R.id.toast_fingerprint_key_invalidated));
                break;
            case OnEncryptionFailed:
                NotifyUtils.notifyBiometricIdentify(this.context, Lang.getString(R.id.toast_fingerprint_password_enc_failed));
                break;
            default:
                L.d("Unknown notifyEnum: " + notifyEnum);
                break;
        }
    }

    public static class IdentifyListener extends XBiometricIdentify.IdentifyListener<BizBiometricIdentify> {
        public IdentifyListener() {
            super(null);
        }

        public IdentifyListener(IdentifyListener parentIdentifyListener) {
            super(parentIdentifyListener);
        }
    }
}
