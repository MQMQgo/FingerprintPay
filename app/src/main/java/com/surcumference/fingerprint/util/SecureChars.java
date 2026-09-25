// Added by mqmqgo, 2026-09-25: helpers keeping the payment password in wipeable char[]/byte[] only
package com.surcumference.fingerprint.util;

import android.text.Editable;

import androidx.annotation.Nullable;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * The payment password is only ever held in char[] / byte[] buffers that are wiped with
 * {@link Arrays#fill} as soon as they are no longer needed; it is never turned into a
 * {@link String} (immutable, cannot be wiped).
 *
 * Limits (best effort only): ART's garbage collector may already have copied/moved an array
 * before it is wiped, the Keystore/Cipher implementation keeps its own internal buffers, and the
 * host app's (WeChat's) own input widgets/buffers receiving the simulated key taps are outside
 * our control.
 */
public final class SecureChars {

    private SecureChars() {
    }

    public static void wipe(@Nullable char[] chars) {
        if (chars != null) {
            Arrays.fill(chars, '\0');
        }
    }

    public static void wipe(@Nullable byte[] bytes) {
        if (bytes != null) {
            Arrays.fill(bytes, (byte) 0);
        }
    }

    /** Copies the Editable content into a new char[] without calling toString(). */
    public static char[] fromEditable(@Nullable Editable editable) {
        if (editable == null) {
            return new char[0];
        }
        char[] out = new char[editable.length()];
        editable.getChars(0, out.length, out, 0);
        return out;
    }

    /** Clears an Editable holding secret input. */
    public static void clear(@Nullable Editable editable) {
        if (editable == null) {
            return;
        }
        try {
            // overwrite in place first (best effort), then drop the content
            int len = editable.length();
            if (len > 0) {
                char[] zeros = new char[len];
                Arrays.fill(zeros, '0');
                editable.replace(0, len, CharBuffer.wrap(zeros));
            }
        } catch (Throwable ignore) {
        }
        editable.clear();
    }

    public static boolean contentEquals(@Nullable char[] chars, String constant) {
        if (chars == null || chars.length != constant.length()) {
            return false;
        }
        int diff = 0;
        for (int i = 0; i < chars.length; i++) {
            diff |= chars[i] ^ constant.charAt(i);
        }
        return diff == 0;
    }

    /** UTF-8 encodes into an exactly sized byte[]; every intermediate buffer is wiped. */
    public static byte[] encodeUtf8(char[] chars) throws CharacterCodingException {
        CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
        byte[] tmp = new byte[(int) Math.ceil(chars.length * (double) encoder.maxBytesPerChar()) + 1];
        try {
            ByteBuffer out = ByteBuffer.wrap(tmp);
            CoderResult r = encoder.encode(CharBuffer.wrap(chars), out, true);
            if (r.isError()) {
                r.throwException();
            }
            r = encoder.flush(out);
            if (r.isError()) {
                r.throwException();
            }
            return Arrays.copyOf(tmp, out.position());
        } finally {
            wipe(tmp);
        }
    }

    /** UTF-8 decodes bytes[off, off+len) into an exactly sized char[]; intermediates are wiped. */
    public static char[] decodeUtf8(byte[] bytes, int off, int len) throws CharacterCodingException {
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
        char[] tmp = new char[len + 1];
        try {
            CharBuffer out = CharBuffer.wrap(tmp);
            CoderResult r = decoder.decode(ByteBuffer.wrap(bytes, off, len), out, true);
            if (r.isError()) {
                r.throwException();
            }
            r = decoder.flush(out);
            if (r.isError()) {
                r.throwException();
            }
            return Arrays.copyOf(tmp, out.position());
        } finally {
            wipe(tmp);
        }
    }

    /** '0'..'9' -> 0..9, otherwise -1. */
    public static int digitIndex(char c) {
        return (c >= '0' && c <= '9') ? c - '0' : -1;
    }
}
