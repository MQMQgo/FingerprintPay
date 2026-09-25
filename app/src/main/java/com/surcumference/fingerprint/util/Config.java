// Modified by mqmqgo, 2026-09-25: Keystore blob storage, removed ANDROID_ID/AES software layer
package com.surcumference.fingerprint.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.surcumference.fingerprint.BuildConfig;
import com.surcumference.fingerprint.util.log.L;
import com.wei.android.lib.fingerprintidentify.util.CryptoObjectHelper;

import java.util.WeakHashMap;

/**
 * Created by Jason on 2017/9/9.
 */

public class Config {

    /** Keystore alias prefix; ".a"/".b" slots are alternated on re-encryption. */
    public static final String KEY_ALIAS_BASE = "fpp.wechat.v2";
    private static final String KEY_PASSWORD_BLOB = "password_v2";
    private static final String KEY_PASSWORD_ALIAS = "password_v2_alias";
    private static final String LEGACY_KEY_PASSWORD = "password";
    private static final String LEGACY_KEY_PASSWORD_IV = "password_iv";
    private static final String LEGACY_KEY_BIOMETRIC_API = "biometric_api";

    private static WeakHashMap<Context, ObjectCache> sConfigCache = new WeakHashMap<>();

    public static Config from(Context context) {
        return new Config(context);
    }

    private ObjectCache mCache;

    private Config(Context context) {
        if (sConfigCache.containsKey(context)) {
            mCache = sConfigCache.get(context);
        }
        if (mCache == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID + ".settings", Context.MODE_PRIVATE);
            // Hardened build: legacy (ECB/ANDROID_ID wrapped) ciphertext is never migrated.
            if (sharedPreferences.contains(LEGACY_KEY_PASSWORD) || sharedPreferences.contains(LEGACY_KEY_PASSWORD_IV)
                    || sharedPreferences.contains(LEGACY_KEY_BIOMETRIC_API)) {
                sharedPreferences.edit()
                        .remove(LEGACY_KEY_PASSWORD)
                        .remove(LEGACY_KEY_PASSWORD_IV)
                        .remove(LEGACY_KEY_BIOMETRIC_API)
                        .remove("skip_version")
                        .commit();
            }
            SharedPreferences mainAppSharePreference;
            try {
                mainAppSharePreference = XPreferenceProvider.getRemoteSharedPreference(context);
            } catch (Exception e) {
                mainAppSharePreference = sharedPreferences;
                L.e(e);
            }
            mCache = new ObjectCache(sharedPreferences, mainAppSharePreference);
            sConfigCache.put(context, mCache);
        }
    }

    public boolean isOn() {
        return mCache.sharedPreferences.getBoolean("switch_on1", false);
    }

    public void setOn(boolean on) {
        mCache.sharedPreferences.edit().putBoolean("switch_on1", on).apply();
    }

    /**
     * @return Base64(IV[12] || AES-256-GCM ciphertext+tag), produced by a hardware-backed,
     * biometric-bound Android Keystore key. Never the plain password.
     */
    @Nullable
    public String getPasswordEncrypted() {
        String enc = mCache.sharedPreferences.getString(KEY_PASSWORD_BLOB, null);
        if (TextUtils.isEmpty(enc) || TextUtils.isEmpty(getPasswordKeyAlias())) {
            return null;
        }
        return enc;
    }

    /** Keystore alias the currently stored blob is bound to, or null. */
    @Nullable
    public String getPasswordKeyAlias() {
        String alias = mCache.sharedPreferences.getString(KEY_PASSWORD_ALIAS, null);
        if (TextUtils.isEmpty(alias)) {
            return null;
        }
        return alias;
    }

    /** Alias to use for a new encryption, so a cancelled re-encryption never destroys the current key. */
    public String getNextPasswordKeyAlias() {
        String current = getPasswordKeyAlias();
        String a = KEY_ALIAS_BASE + ".a";
        String b = KEY_ALIAS_BASE + ".b";
        return a.equals(current) ? b : a;
    }

    public void setPasswordEncrypted(String blob, String keyAlias) {
        mCache.sharedPreferences.edit()
                .putString(KEY_PASSWORD_BLOB, blob)
                .putString(KEY_PASSWORD_ALIAS, keyAlias)
                .commit();
    }

    /** Removes the stored ciphertext and deletes both Keystore keys. */
    public void clearPassword() {
        mCache.sharedPreferences.edit()
                .remove(KEY_PASSWORD_BLOB)
                .remove(KEY_PASSWORD_ALIAS)
                .commit();
        CryptoObjectHelper.removeKey(KEY_ALIAS_BASE + ".a");
        CryptoObjectHelper.removeKey(KEY_ALIAS_BASE + ".b");
    }

    public boolean isShowFingerprintIcon() {
        return mCache.sharedPreferences.getBoolean("fingerprint_icon", false);
    }

    public void setShowFingerprintIcon(boolean on) {
        mCache.sharedPreferences.edit().putBoolean("fingerprint_icon", on).apply();
    }

    public boolean isVolumeDownMonitorEnabled() {
        return mCache.sharedPreferences.getBoolean("volume_down_monitor_enabled", true);
    }

    public void setVolumeDownMonitorEnabled(boolean on) {
        mCache.sharedPreferences.edit().putBoolean("volume_down_monitor_enabled", on).apply();
    }

    public void setLicenseAgree(boolean agree) {
        mCache.sharedPreferences.edit().putBoolean("license_agree", agree).apply();
        mCache.mainAppSharedPreferences.edit().putBoolean("license_agree", agree).apply();
    }

    public boolean getLicenseAgree() {
        boolean agree = mCache.mainAppSharedPreferences.getBoolean("license_agree", false);
        if (!agree) {
            agree = mCache.sharedPreferences.getBoolean("license_agree", false);
        }
        return agree;
    }

    public void commit() {
        mCache.sharedPreferences.edit().commit();
        mCache.mainAppSharedPreferences.edit().commit();
    }

    private class ObjectCache {
        SharedPreferences sharedPreferences;
        SharedPreferences mainAppSharedPreferences;

        public ObjectCache(SharedPreferences sharedPreferences, SharedPreferences mainAppSharedPreferences) {
            this.sharedPreferences = sharedPreferences;
            this.mainAppSharedPreferences = mainAppSharedPreferences;
        }
    }
}
