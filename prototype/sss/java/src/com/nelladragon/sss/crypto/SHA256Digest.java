// Copyright (C) 2017 Peter Robinson
package com.nelladragon.sss.crypto;


import java.security.MessageDigest;

/**
 */
public class SHA256Digest {
    private static MessageDigest md = getInstance();
    private static MessageDigest getInstance() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };


    public static byte[] digest(byte[] data1) throws Exception {
        md.reset();
        return md.digest(data1);
    }

}
