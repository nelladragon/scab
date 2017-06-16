package com.nelladragon.sss.password;

import com.nelladragon.sss.util.Util;

import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * Methods for hardening passwords.
 */
public class PasswordHardening {
    private static MessageDigest md = getMdInstance();

    private static MessageDigest getMdInstance() {
        try {
            return MessageDigest.getInstance("SHA-512");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    ;

    private static SecureRandom rand = getRandInstance();

    private static SecureRandom getRandInstance() {
        try {
            SecureRandom rand1 = SecureRandom.getInstance("SHA1PRNG");
            Util.addQuickEntropy(rand1);
            return rand1;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Harden the password. For the purposes of the prototype, this is just a simple salted hash.
     *
     * @param password The password to harden.
     * @return [0] Hardened password.
     * [1] Parameters related to password hardening (salt).
     * @throws Exception
     */
    public static byte[][] mainPasswordHardenGenerate(byte[] password) throws Exception {
        byte[] salt = new byte[32];
        Util.addQuickEntropy(rand);
        rand.nextBytes(salt);
        md.reset();
        md.update(salt);
        byte[][] result = new byte[2][];
        result[0] = md.digest(password);
        result[1] = salt;
        return result;
    }

    /**
     * Harden the password. For the purposes of the prototype, this is just a simple salted hash.
     *
     * @param password The password to harden.
     * @param salt     The salt to use with hardened password.
     * @return Hardened password.
     * @throws Exception
     */
    public static byte[] mainPasswordHardenRegenerate(byte[] password, byte[] salt) throws Exception {
        md.reset();
        md.update(salt);
        return md.digest(password);
    }


    /**
     * Harden the backup password. For the purposes of the prototype, this is just a simple salted hash.
     *
     * @param password The password to harden.
     * @return [0] Hardened password.
     * [1] Parameters related to password hardening (salt).
     * @throws Exception
     */
    public static byte[][] backupPasswordHardenGenerate(byte[] password) throws Exception {
        return mainPasswordHardenGenerate(password);
    }

    /**
     * Harden the password. For the purposes of the prototype, this is just a simple salted hash.
     *
     * @param password The password to harden.
     * @param salt     The salt to use with hardened password.
     * @return Hardened password.
     * @throws Exception
     */
    public static byte[] backupPasswordHardenRegenerate(byte[] password, byte[] salt) throws Exception {
        return mainPasswordHardenRegenerate(password, salt);
    }

}
