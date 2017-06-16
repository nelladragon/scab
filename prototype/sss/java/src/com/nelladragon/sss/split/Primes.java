// Copyright (C) 2017 Peter Robinson
package com.nelladragon.sss.split;

import java.math.BigInteger;

/**
 * Well known primes The prime to be used must be larger that the derived secret.
 *
 *
 */
public class Primes {
    private static final String P128S =
        "0200000000000000000000000000000011";
    private static final String P256S =
        "0200000000000000000000000000000000000000000000000000000000000000" +
        "9b";
    private static final String P384S =
        "0200000000000000000000000000000000000000000000000000000000000000" +
        "00000000000000000000000000000000bd";
    private static final String P512S =
        "0200000000000000000000000000000000000000000000000000000000000000" +
        "0000000000000000000000000000000000000000000000000000000000000000" +
        "9f";
    private static final String P768S =
        "0200000000000000000000000000000000000000000000000000000000000000" +
        "0000000000000000000000000000000000000000000000000000000000000000" +
        "0000000000000000000000000000000000000000000000000000000000000000" +
        "11";
    private static final String P1024S =
        "0200000000000000000000000000000000000000000000000000000000000000" +
        "0000000000000000000000000000000000000000000000000000000000000000" +
        "0000000000000000000000000000000000000000000000000000000000000000" +
        "0000000000000000000000000000000000000000000000000000000000000005" +
        "c9";


//    public static final BigInteger P128 = new BigInteger(P128S, 16);
//    public static final BigInteger P256 = new BigInteger(P256S, 16);
    public static final BigInteger P384 = new BigInteger(P384S, 16);
//    public static final BigInteger P512 = new BigInteger(P512S, 16);
//    public static final BigInteger P768 = new BigInteger(P768S, 16);
//    public static final BigInteger P1024 = new BigInteger(P1024S, 16);

    // The prime should be bigger than the keys being used (AES256).
    // A "P256" prime is actually 258 bits long, and hence will be fine for
    // use with 256 bit keys.
    public static final BigInteger PRIME = P384;

//    public static BigInteger getPrime(int bits) {
//        switch (bits) {
//        case 128:
//            return P128;
//        case 256:
//            return P256;
//        case 384:
//            return P384;
//        case 512:
//            return P512;
//        case 768:
//            return P768;
//        case 1024:
//            return P1024;
//        default:
//            throw new Error("unsupported prime size: " + bits);
//        }
//    }

}
