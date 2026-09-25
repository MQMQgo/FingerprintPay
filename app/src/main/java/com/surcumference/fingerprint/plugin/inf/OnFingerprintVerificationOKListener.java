// Modified by mqmqgo, 2026-09-25: password passed as wipeable char[] instead of String
package com.surcumference.fingerprint.plugin.inf;

public interface OnFingerprintVerificationOKListener {
    /**
     * @param password decrypted password. Only valid during this call: the caller wipes the array
     *                 right after it returns. Implementations that need it asynchronously must
     *                 {@code clone()} it and wipe their copy themselves.
     */
    void onFingerprintVerificationOK(char[] password);
}
