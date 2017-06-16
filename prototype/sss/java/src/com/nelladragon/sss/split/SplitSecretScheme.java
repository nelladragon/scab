// Copyright (C) 2017 Peter Robinson
package com.nelladragon.sss.split;

import com.nelladragon.sss.util.Util;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Top level class which manages the split secret system.
 *
 */
public class SplitSecretScheme {
    SecureRandom random;

    // Prime will be fixed for a crypto system version.
    BigInteger PRIME = Primes.PRIME;

    public static final int DEFAULT_THRESHOLD = 4;
    public static final int DEFAULT_NUM_SHARES = 6;

    public SplitSecretScheme() throws Exception {
        this.random = SecureRandom.getInstance("SHA1PRNG");

    }

    /**
     * Generate a random secret and random shares given a fixed password share.
     *
     * @param passwordShare The fixed password share.
     * @return The split secret.
     * @throws Exception
     */
    public SplitKey generate(SecretShare passwordShare) throws Exception {
        int threshold = DEFAULT_THRESHOLD;
        int numberOfShares = DEFAULT_NUM_SHARES;

        if (threshold < 2 || threshold > numberOfShares) {
            throw new Exception("invalid config");
        }
        // Number of shares includes the fixed password share. We already know what this value is,
        // so we don't need to waste time generating it again.
        int numRandomShares = numberOfShares - 1;


        BigInteger[] coefs = FixedShareThresholdScheme.generateRandomCoefficients(PRIME, threshold, this.random);
        BigInteger[] xValues = FixedShareThresholdScheme.generateXvalues(PRIME, numRandomShares, this.random);
        BigInteger[] yValsAndSecret = FixedShareThresholdScheme.generateSharesGivenPasswordShare(
                PRIME, passwordShare.getShareY(), xValues, coefs);

        BigInteger keyValue = yValsAndSecret[0];
        SecretShare deviceShare = new SecretShare(ShareType.DEVICE, xValues[0], yValsAndSecret[1]);
        SecretShare[] externalShares = new SecretShare[numRandomShares - 1];
        for (int i = 0; i < numRandomShares-1; i++) {
            externalShares[i] = new SecretShare(ShareType.CLOUD, xValues[i+1], yValsAndSecret[i+2]);
        }
        return new SplitKey(keyValue, passwordShare, deviceShare, externalShares);

    }


    /**
     * Recover the secret based on shares.
     *
     * @param shares Shares to use to recover the secret.
     * @return The recovered secret.
     * @throws Exception
     */
    public BigInteger recoverSecret(SecretShare[] shares) throws Exception {
        if (shares.length < DEFAULT_THRESHOLD) {
            throw new Exception("not enough shares to recover the secret");
        }
        BigInteger[] xValues = new BigInteger[shares.length];
        BigInteger[] yValues = new BigInteger[shares.length];
        for (int i = 0; i < shares.length; i++) {
            xValues[i] = shares[i].getShareX();
            yValues[i] = shares[i].getShareY();
        }
        return FixedShareThresholdScheme.recoverSecret(PRIME, xValues, yValues, BigInteger.ZERO);
    }


    /**
     * Generate a new share based on the existing shares.
     *
     * @param shares Existing shares.
     * @return New share.
     * @throws Exception
     */
    public SecretShare addShare(SecretShare[] shares) throws Exception {
        if (shares.length < DEFAULT_THRESHOLD) {
            throw new Exception("not enough shares to create a new share");
        }
        BigInteger[] xValues = new BigInteger[shares.length];
        BigInteger[] yValues = new BigInteger[shares.length];
        for (int i = 0; i < shares.length; i++) {
            xValues[i] = shares[i].getShareX();
            yValues[i] = shares[i].getShareY();
        }
        BigInteger newXvalue = FixedShareThresholdScheme.generateXval(PRIME, xValues, xValues.length, this.random);
        BigInteger yValue = FixedShareThresholdScheme.recoverSecret(PRIME, xValues, yValues, newXvalue);
        return new SecretShare(ShareType.CLOUD, newXvalue, yValue);
    }


    /**
     * Proactize shares. This updates the shares such that old shares and
     * new shares can't be used together to recover the secret.
     *
     * @param shares Old shares to be proactivized.
     * @return New updated / proactivized shares.
     * @throws Exception
     */
    public SecretShare[] proactivizeShares(SecretShare[] shares) throws Exception {
        for (SecretShare share: shares) {
           if (share.getType() == ShareType.PASSWORD) {
               throw new Exception("Don't pass password share to the proactivization code!");
           }
        }

        // Generate shares to be used with proactivization.
        BigInteger[] proactCoefs = FixedShareThresholdScheme.generateProactivizationCoefficients(PRIME, DEFAULT_THRESHOLD, this.random);

        // Extract the X values from the shares.
        BigInteger[] xValues = new BigInteger[shares.length];
        for (int i=0; i < shares.length; i++) {
            xValues[i] = shares[i].getShareX();
        }

        BigInteger[] proactShares = FixedShareThresholdScheme.generateShares(PRIME, BigInteger.ZERO, xValues, proactCoefs);

        // Proactivize the values.
        for (int i=0; i < shares.length; i++) {
            BigInteger newY = shares[i].getShareY();
            newY = newY.add(proactShares[i]);
            newY = newY.mod(PRIME);

            shares[i] = new SecretShare(shares[i].getType(), shares[i].getShareX(), newY);
        }

        return shares;
    }



    /**
     * Change the password share and proactize all other shares.
     * The secret secret stays unchanged.
     *
     * @param shares Shares to be proactivized.
     * @param oldPassword Old password share.
     * @param newPassword New password share.
     * @return Updated / proactivized shares.
     * @throws Exception
     */
    public SecretShare[] changePasswordAndProactivizeShares(SecretShare[] shares, SecretShare oldPassword, SecretShare newPassword) throws Exception {
        for (SecretShare share: shares) {
            if (share.getType() == ShareType.PASSWORD) {
                throw new Exception("Don't pass password share to the proactivization code!");
            }
        }

        if (oldPassword.getType() != ShareType.PASSWORD) {
            throw new Exception("Invalid password share");
        }
        if (newPassword.getType() != ShareType.PASSWORD) {
            throw new Exception("Invalid password share");
        }
        BigInteger passChangeValue = newPassword.getShareY();
        passChangeValue = passChangeValue.subtract(oldPassword.getShareY());
        passChangeValue = passChangeValue.mod(PRIME);

        // Generate shares to be used with proactivization.
        BigInteger[] proactCoefs = FixedShareThresholdScheme.generatePasswordChangeCoefficients(
                PRIME, DEFAULT_THRESHOLD, this.random, passChangeValue);

        // Extract the X values from the shares.
        BigInteger[] xValues = new BigInteger[shares.length];
        for (int i=0; i < shares.length; i++) {
            xValues[i] = shares[i].getShareX();
        }

        BigInteger[] proactShares = FixedShareThresholdScheme.generateShares(PRIME, BigInteger.ZERO, xValues, proactCoefs);

        // Proactive the values.
        for (int i=0; i < shares.length; i++) {
            BigInteger newY = shares[i].getShareY();
            newY = newY.add(proactShares[i]);
            newY = newY.mod(PRIME);

            shares[i] = new SecretShare(shares[i].getType(), shares[i].getShareX(), newY);
        }

        return shares;
    }
}
