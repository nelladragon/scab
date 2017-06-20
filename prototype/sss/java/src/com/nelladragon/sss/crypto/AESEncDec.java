package com.nelladragon.sss.crypto;

import com.nelladragon.sss.util.Util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * AES processing.
 */
public class AESEncDec {


    public SecretKey generateKey(BigInteger rawSecret) throws Exception {
        // Convert raw secret to a 256 bit number. We could simply truncate.
        // Putting it through a one way function seems like a good idea.
        byte[] rawKey = SHA256Digest.digest(rawSecret.toByteArray());

        Util.printBuffer(rawKey);

        return new SecretKeySpec(rawKey, "AES");
    }

    public static final int GCM_NONCE_LENGTH = 12; // in bytes
    public static final int GCM_TAG_LENGTH = 16; // in bytes

    public byte[][] authEncrypt(SecretKey key, byte[] plainText) throws Exception {
        Cipher aesEncrypt = Cipher.getInstance("AES/GCM/NoPadding");

        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        final byte[] nonce = new byte[GCM_NONCE_LENGTH];
        random.nextBytes(nonce);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce);
        aesEncrypt.init(Cipher.ENCRYPT_MODE, key, spec);

        return new byte[][] {aesEncrypt.doFinal(plainText), nonce};
    }


    public byte[] authDecrypt(SecretKey key, byte[] cipherText, byte[] nonce) throws Exception {
        Cipher aesDecrypt = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce);
        aesDecrypt.init(Cipher.DECRYPT_MODE, key, spec);

        return aesDecrypt.doFinal(cipherText);
    }

}
