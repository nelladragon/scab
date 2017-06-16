package com.rsa.peter.testsplitsecret.util;

import com.rsa.jsafe.provider.JsafeJCE;

import java.security.MessageDigest;
import java.security.Provider;

/**
 * Created by robinp2 on 23/07/2014.
 */
public class SHA256Digest {
    public static final Provider JCE = new JsafeJCE();


    public static byte[] digest(byte[] data1) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA256", JCE);

        md.update(data1);
        byte[] digest = md.digest();
        // TODO clear sensitive data
        return digest;
    }

}
