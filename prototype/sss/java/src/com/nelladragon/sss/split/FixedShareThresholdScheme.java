// Copyright (C) 2017 Peter Robinson
package com.nelladragon.sss.split;

import java.math.BigInteger;

import com.nelladragon.sss.util.*;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;


/**
 * Threshold scheme which is similar to Shamir's scheme. The difference is that
 * there are three fixed shares.
 *
 * Threshold is 6. Hence the equation is:
 *  y = a*x^5 + b*x^4 + c*x^3 + d*x^2 + e*x + f  mod p
 *
 * The fixed shares are:
 * Password Share, which has an X value of 1.
 * Backup Share which has an X value of -1.
 * Device Share which has an X value of 2.
 * Note that the derived secret is the Y value at X=0.
 *
 * There are also random shares. These always have the same X values, ensuring that an
 * attack can't force multiple random shares to be created.
 *
 * Transfer Share: X value of 3.
 * Cloud Share1: X value of 4.
 * Cloud Share2: X value of 5.
 * Cloud Share3: X value of 6.
 * Cloud Share4: X value of 7.
 *
 */
public final class FixedShareThresholdScheme {

    // Prime will be fixed for a crypto system version.
    private static final BigInteger PRIME = Primes.PRIME;

    public static final int THRESHOLD = 6;

    // X values for various shares.
    public static final int X_SECRET = 0;
    public static final int X_PASSWORD = 1;
    public static final int X_BACKUP = 3;
    public static final int X_DEVICE = 2;
    public static final int X_TRANSFER = 10;
    public static final int X_CLOUD1 = 11;
    public static final int X_CLOUD2 = 12;
    public static final int X_CLOUD3 = 13;
    public static final int X_CLOUD4 = 14;



    public static final BigInteger X_BIG_PASSWORD = new BigInteger(new byte[] {X_PASSWORD});
    public static final BigInteger X_BIG_BACKUP = new BigInteger(new byte[] {X_BACKUP});
    private static final BigInteger X_BIG_DEVICE = new BigInteger(new byte[] {X_DEVICE});
    private static final BigInteger X_BIG_TRANSFER = new BigInteger(new byte[] {X_TRANSFER});
    private static final BigInteger X_BIG_CLOUD1 = new BigInteger(new byte[] {X_CLOUD1});
    private static final BigInteger X_BIG_CLOUD2 = new BigInteger(new byte[] {X_CLOUD2});
    private static final BigInteger X_BIG_CLOUD3 = new BigInteger(new byte[] {X_CLOUD3});
    private static final BigInteger X_BIG_CLOUD4 = new BigInteger(new byte[] {X_CLOUD4});

    private static final BigInteger TWO = new BigInteger(new byte[] {2});
    private static final BigInteger ONE_ON_TWO = TWO.modInverse(PRIME);



    SecureRandom random;



    public FixedShareThresholdScheme() throws Exception {
        this.random = SecureRandom.getInstance("SHA1PRNG");

    }



    /**
     * Generate the coefficients used to construct the polynomial randomly.
     * <p>
     * The coefficients are randomly generated in the range
     * (2^((length of p) - 2)  to  2^((length of p) - 1))
     * offset to (p - 1) - 2^((length of p) - 1).
     *
     * What this does is ensure the coefficients are large and the modulo arithmetic occurs.
     *
     * @param numCoefficients The number of coefficients to generate.
     * @return An array of <code>MPInteger</code>s containing the coefficients.
     */
    private BigInteger[] generateRandomCoefficients(int numCoefficients) {
        BigInteger[] coefficients = new BigInteger[numCoefficients];
        int pLength = PRIME.bitLength();
        int coefficientSize = (pLength + 7) / 8;
        byte[] coefficientData = new byte[coefficientSize];
        byte[] maxCandidateData = new byte[coefficientSize];

        // The generated bits will always be 1 bit shorter than p. With the most significant
        // bit set to 1. This ensures that the coefficients are large.
        // Use bit masks to make the generated data the correct length.
        byte maxBitMask = 0x00;
        byte maxByteMask = 0x00;
        int msBits = (pLength-1) % 8;
        for (int i=0; i<msBits; i++) {
            if (i==0) {
                maxBitMask = 0x01;
            } else {
                maxBitMask <<= 1;
            }
            maxByteMask |= maxBitMask;
        }

        // Create the max possible generated coefficient
        Arrays.fill(maxCandidateData, 0, coefficientSize, (byte) 0xFF);
        maxCandidateData[0] = maxByteMask;

        // Calculate the difference between the value of the max generated data and p-1.
        BigInteger maxCandidate = new BigInteger(maxCandidateData);
        BigInteger offset = PRIME.subtract(maxCandidate);
        offset = offset.subtract(BigInteger.ONE);

        for(int i = 0; i < numCoefficients; i++){
            // Generate a random coefficient.
            this.random.nextBytes(coefficientData);
            // Remove extra bits
            coefficientData[0] &= maxByteMask;
            // Set msb to 1
            coefficientData[0] |= maxBitMask;
            coefficients[i] = new BigInteger(coefficientData);
            // Add the offset to make the maximum value p-1.
            coefficients[i] = coefficients[i].add(offset);
        }
        return coefficients;
    }


    /**
     * Randomly generate the secret value (the secret) and the Y values for all X values,
     * given the fixed password secret and fixed backup secret.
     *
     * @param passwordShare Share derived from user's password.
     * @param backupShare  Share derived from user's backup password.
     * @return The resulting split key and shares.
     */
    public SplitKey generateFirstTimeRegistration(
            final SecretShare passwordShare, final SecretShare backupShare) {

        // Calculate coefficients and then shares.
        // Randomly generate a1, b1, c1, and d1.
        final int A_OFS = 0;
        final int B_OFS = 1;
        final int C_OFS = 2;
        final int D_OFS = 3;
        BigInteger[] coefficients = generateRandomCoefficients(THRESHOLD-2);

        // Calculate f1.
        // f1 = (3*yP + 240*a1 + 78*b1 + 24*c1 + 6*d1 - yB) / 2
        BigInteger acc1 = passwordShare.getShareY();
        acc1 = acc1.multiply(BigInteger.valueOf(3));
        acc1 = acc1.mod(PRIME);

        BigInteger acc2 = coefficients[A_OFS];
        acc2 = acc2.multiply(BigInteger.valueOf(240));
        acc2 = acc2.mod(PRIME);

        BigInteger acc3 = coefficients[B_OFS];
        acc3 = acc3.multiply(BigInteger.valueOf(78));
        acc3 = acc3.mod(PRIME);

        BigInteger acc4 = coefficients[C_OFS];
        acc4 = acc4.multiply(BigInteger.valueOf(24));
        acc4 = acc4.mod(PRIME);

        BigInteger acc5 = coefficients[D_OFS];
        acc5 = acc5.multiply(BigInteger.valueOf(6));
        acc5 = acc5.mod(PRIME);

        BigInteger acc6 = backupShare.getShareY();
        acc6 = acc6.negate();
        acc6 = acc6.mod(PRIME);

        BigInteger accumulator = acc1.add(acc2);
        accumulator = accumulator.mod(PRIME);
        accumulator = accumulator.add(acc3);
        accumulator = accumulator.mod(PRIME);
        accumulator = accumulator.add(acc4);
        accumulator = accumulator.mod(PRIME);
        accumulator = accumulator.add(acc5);
        accumulator = accumulator.mod(PRIME);
        accumulator = accumulator.add(acc6);
        accumulator = accumulator.mod(PRIME);
        accumulator = accumulator.multiply(ONE_ON_TWO);
        accumulator = accumulator.mod(PRIME);
        BigInteger secret = accumulator;


        // Calculate e1.
        // e1 = yP - a1 - b1 - c1 - d1 - f1
        accumulator = passwordShare.getShareY();
        accumulator = accumulator.subtract(coefficients[A_OFS]);
        accumulator = accumulator.mod(PRIME);
        accumulator = accumulator.subtract(coefficients[B_OFS]);
        accumulator = accumulator.mod(PRIME);
        accumulator = accumulator.subtract(coefficients[C_OFS]);
        accumulator = accumulator.mod(PRIME);
        accumulator = accumulator.subtract(coefficients[D_OFS]);
        accumulator = accumulator.mod(PRIME);
        accumulator = accumulator.subtract(secret);
        accumulator = accumulator.mod(PRIME);
        BigInteger e1 = accumulator;

        BigInteger[] allCoefs = new BigInteger[] {coefficients[A_OFS], coefficients[B_OFS], coefficients[C_OFS], coefficients[D_OFS], e1};



        // Set-up the X values that need to be generated.
        BigInteger[] xValues = new BigInteger[] {X_BIG_DEVICE, X_BIG_CLOUD1, X_BIG_CLOUD2, X_BIG_CLOUD3, X_BIG_CLOUD4};

        BigInteger[] yValues = generateShares(secret, xValues, allCoefs);

        SecretShare deviceShare = new SecretShare(ShareType.DEVICE, xValues[0], yValues[0]);
        SecretShare cloudShare1 = new SecretShare(ShareType.CLOUD1, xValues[1], yValues[1]);
        SecretShare cloudShare2 = new SecretShare(ShareType.CLOUD2, xValues[2], yValues[2]);
        SecretShare cloudShare3 = new SecretShare(ShareType.CLOUD3, xValues[3], yValues[3]);
        SecretShare cloudShare4 = new SecretShare(ShareType.CLOUD4, xValues[4], yValues[4]);

        Map<ShareType, SecretShare> shares = new HashMap<>();
        shares.put(ShareType.DEVICE, deviceShare);
        shares.put(ShareType.CLOUD1, cloudShare1);
        shares.put(ShareType.CLOUD2, cloudShare2);
        shares.put(ShareType.CLOUD3, cloudShare3);
        shares.put(ShareType.CLOUD4, cloudShare4);

        return new SplitKey(secret, shares);
    }



    /**
     * Recover the secret based on shares.
     *
     * @param shares Shares to use to recover the secret.
     * @return The recovered secret.
     */
    public BigInteger calculateSecret(SecretShare[] shares) {
        return calculateShare(shares, BigInteger.ZERO);
    }


    /**
     * Recover the transfer share based on shares.
     *
     * @param shares Shares to use to recover the transfer share.
     * @return The recovered share.
     */
    public SecretShare calculateTransferShare(SecretShare[] shares) {
        BigInteger transferShareY = calculateShare(shares, X_BIG_TRANSFER);
        return new SecretShare(ShareType.TRANSFER, X_BIG_TRANSFER, transferShareY);
    }

    /**
     * Recover the device share based on shares.
     *
     * @param shares Shares to use to recover the device share.
     * @return The recovered share.
     */
    public SecretShare calculateDeviceShare(SecretShare[] shares) {
        BigInteger deviceShareY = calculateShare(shares, X_BIG_DEVICE);
        return new SecretShare(ShareType.TRANSFER, X_BIG_DEVICE, deviceShareY);
    }


    /**
     * Proactivize cloud shares whilst keeping password, backup, and device shares unchanged.
     *
     * @param cloudShares Current cloud shares.
     * @return Updated cloud shares.
     */
    public SecretShare[] proactivizeCloudShares(SecretShare[] cloudShares) {
        return proactivizeCloudShares(BigInteger.ZERO, BigInteger.ZERO, cloudShares);
    }


    /**
     * Update password based on old password, device share, and cloud shares.
     *
     * @param cloudShares Current cloud shares.
     * @return Updated cloud shares.
     */
    public SecretShare[] changePassword(SecretShare oldPassword, SecretShare newPassword, SecretShare[] cloudShares) {
        BigInteger newPy = newPassword.getShareY();
        BigInteger oldPy = oldPassword.getShareY();
        BigInteger acc = newPy.subtract(oldPy);
        acc = acc.mod(PRIME);

        return proactivizeCloudShares(acc, BigInteger.ZERO, cloudShares);
    }



    /**
     *
     * @param dP Password share update mod p
     * @param dB Backup password share update mod p
     * @param shares Cloud shares.
     * @return
     */
    public SecretShare[] proactivizeCloudShares(BigInteger dP, BigInteger dB, SecretShare[] shares) {
        final int CLOUD1_OFS = 0;
        final int CLOUD2_OFS = 1;
        final int CLOUD3_OFS = 2;
        final int CLOUD4_OFS = 3;


        if (    shares.length != 4 ||
                shares[CLOUD1_OFS].getType() != ShareType.CLOUD1 ||
                shares[CLOUD2_OFS].getType() != ShareType.CLOUD2 ||
                shares[CLOUD3_OFS].getType() != ShareType.CLOUD3 ||
                shares[CLOUD4_OFS].getType() != ShareType.CLOUD4) {
            throw new RuntimeException("Only proactivize cloud shares with this method");
        }

        // Calculate coefficients and then shares.
        // Randomly generate a2, b2
        final int A_OFS = 0;
        final int B_OFS = 1;
        BigInteger[] randomCoefs = generateRandomCoefficients(2);

        //c2 = (dB - a2 * 150 - b1 * 36 + dP * 3) / 6
        BigInteger acc1 = randomCoefs[A_OFS].negate();
        acc1 = acc1.mod(PRIME);
        acc1 = acc1.multiply(BigInteger.valueOf(150));
        acc1 = acc1.mod(PRIME);

        BigInteger acc2 = randomCoefs[B_OFS].negate();
        acc2 = acc2.mod(PRIME);
        acc2 = acc2.multiply(BigInteger.valueOf(36));
        acc2 = acc2.mod(PRIME);

        BigInteger accumulator = dP.multiply(BigInteger.valueOf(3));
        accumulator = accumulator.add(dB);
        accumulator = accumulator.add(acc1);
        accumulator = accumulator.add(acc2);
        accumulator = accumulator.mod(PRIME);
        BigInteger invSix = BigInteger.valueOf(6).modInverse(PRIME);
        accumulator = accumulator.multiply(invSix);
        BigInteger c2 = accumulator.mod(PRIME);

        // d2 = -(a2*15 + b2*7 + c2*3 + dP)
        BigInteger acc3 = randomCoefs[A_OFS].multiply(BigInteger.valueOf(15));
        BigInteger acc4 = randomCoefs[B_OFS].multiply(BigInteger.valueOf(7));
        BigInteger acc5 = c2.multiply((BigInteger.valueOf(3)));
        accumulator = acc3.add(acc4);
        accumulator = accumulator.add(acc5);
        accumulator = accumulator.add(dP);
        accumulator = accumulator.negate();
        BigInteger d2 = accumulator.mod(PRIME);

        //e2 = dP -a2 - b2 - c2 - d2
        accumulator = dP;
        accumulator = accumulator.subtract(randomCoefs[A_OFS]);
        accumulator = accumulator.mod(PRIME);
        accumulator = accumulator.subtract(randomCoefs[B_OFS]);
        accumulator = accumulator.mod(PRIME);
        accumulator = accumulator.subtract(c2);
        accumulator = accumulator.mod(PRIME);
        accumulator = accumulator.subtract(d2);
        BigInteger e2 = accumulator.mod(PRIME);

        //f2 = 0

        BigInteger[] allCoefs = new BigInteger[] {e2, d2, c2, randomCoefs[B_OFS], randomCoefs[A_OFS]};

        // Set-up the X values that need to be generated.
        BigInteger[] xValues = new BigInteger[] {X_BIG_CLOUD1, X_BIG_CLOUD2, X_BIG_CLOUD3, X_BIG_CLOUD4, X_BIG_PASSWORD, X_BIG_BACKUP, X_BIG_DEVICE, BigInteger.ZERO};

        BigInteger[] yValues = generateShares(BigInteger.ZERO, xValues, allCoefs);

        Util.println("Password Share (should be zero): " + yValues[4]);
        Util.println("Backup Share (should be zero): " + yValues[5]);
        Util.println("Device Share (should be zero): " + yValues[6]);
        Util.println("Secret (should be zero): " + yValues[7]);

        for (int i = 0; i < shares.length; i++) {
            BigInteger yVal = shares[i].getShareY().add(yValues[i]);
            yVal = yVal.mod(PRIME);
            shares[i].updateY(yVal);

        }
        return shares;
    }




    /**
     * Generates Y values given X values using a polynomial which is based on the
     * threshold and a secret value. <code>threshold-1</code> is the degree of
     * the polynomial. The secret is the Y intercept (x=0 value). Coefficients
     * of the polynomial are randomly generated.
     *
     * For <code>threshold = 3</code>, the polynomial is given by:<br>
     * y = secret + a * x + b * x**2
     *
     * The number of shares that the secret is split into is given by the number of X values.
     *
     * @param secret The value to protect. This will be the x=0 value.
     * @param xValues The x coordinates of the shares. Note that none of them should be zero.
     *  All xValues should be unique.
     * @param coefficients The coefficients of the polynomial. All the values should be smaller than p.
     *          There should be threshold-1 coefficients.
     * @return The y coordinates of the shares.
     */
    private static BigInteger[] generateShares(BigInteger secret, BigInteger[] xValues,
            BigInteger[] coefficients) {

        // Calculate the Y values given the X values and coefficients.
        int numShares = xValues.length;
        int numCoefficients = coefficients.length;
        BigInteger[] yVals = new BigInteger[numShares];
        BigInteger k;
        for(int i = 0; i < numShares; i++){
            BigInteger accumulator = BigInteger.ZERO;
            k  = BigInteger.ONE;
            for(int j=0; j < numCoefficients; j++){
                // Calculate coefficient * x ** k
                BigInteger exp = xValues[i].modPow(k, PRIME);
                BigInteger mult = exp.multiply(coefficients[j]);
                k = k.add(BigInteger.ONE);

                // Accumulate the term
                BigInteger add1 = accumulator.add(mult);
                accumulator = add1.mod(PRIME);
            }
            // Accumulate the constant term; which is the secret.
            BigInteger add2 = accumulator.add(secret);
            accumulator = add2.mod(PRIME);

            yVals[i] = accumulator;
        }

        return yVals;
    }




    /**
     * Regenerate the secret based on X values and Y values.
     * <p>
     * The secret is recovered using Lagrange basis polynomials.
     * <p>
     * Note that the wrong secret will be generated if the values are not correct.
     *
     * The Lagrange basis polynomials are of the form:
     * <pre>
     *       x - x1      x - x2     x - x3
     * L0 = -------  *  -------  *  -------  *  ....
     *      x0 - x1     x0 - x2     x0 - x3
     *
     *       x - x0      x - x2      x - x3
     * L1 = -------  *  -------  *  -------  *  ....
     *      x1 - x0     x1 - x2     x1 - x3
     *
     *       x - x0      x - x1      x - x3
     * L2 = -------  *  -------  *  -------  *  ....
     *      x2 - x0     x2 - x1     x2 - x3
     *
     *          x - x0        x - x1        x - x3                       x - xi
     * Ln-1 = ---------  *  ---------  *  ---------  *  ....  where the ------- term is not included.
     *        xn-1 - x0     xn-1 - x1     xn-1 - x3                     xi - xi
     *
     * y(x) = Sum for all j ( y(j) * L(j))
     * </pre>
     * To recover the secret, y is calculated at x=0.
     * To create a new share use a new x value.
     *
     * @param shares Shares to use to reconstruct coefficients.
     * @param x The x value. If x=0 then the secret is recovered, otherwise a new share is created.
     * @return The recovered secret.
     */
    private static BigInteger calculateShare(SecretShare[] shares, BigInteger x) {
        int numShares = shares.length;
        if (numShares < THRESHOLD) {
            throw new RuntimeException("not enough shares to recover the secret");
        }
        if (numShares != THRESHOLD) {
            throw new RuntimeException("Why are too many shares being provided?");
        }

        BigInteger[] xValues = new BigInteger[shares.length];
        BigInteger[] yValues = new BigInteger[shares.length];
        for (int i = 0; i < shares.length; i++) {
            xValues[i] = shares[i].getShareX();
            yValues[i] = shares[i].getShareY();
        }

        // Compute the Lagrange basis polynomials L0 to Ln-1.
        BigInteger[] lagrange = new BigInteger[numShares];
        for(int i = 0; i < numShares; i++){
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;
            for(int j = 0; j < numShares; j++){
                if(j != i){
                    // Numerator = (x-x0) * (x-x1) * (x-x2) * ...
                    BigInteger temp1 = x.subtract(xValues[j]);
                    BigInteger temp2 = temp1.mod(PRIME);
                    BigInteger mult1 = numerator.multiply(temp2);
                    numerator = mult1.mod(PRIME);

                    // Denominator = (xi-x0) * (xi-x1) * (xi-x2) * ...
                    BigInteger temp3 = xValues[i].subtract(xValues[j]);
                    BigInteger temp4 = temp3.mod(PRIME);
                    BigInteger mult2 = denominator.multiply(temp4);
                    denominator = mult2.mod(PRIME);
                }
            }

            // Li = numerator / denominator
            BigInteger temp5 = denominator.modInverse(PRIME);
            BigInteger temp6 = numerator.multiply(temp5);
            lagrange[i] = temp6.mod(PRIME);
        }

        // recovered = (y0 * L0) + (y1 * L1) +...
        BigInteger recovered = BigInteger.ZERO;
        for(int i=0; i < numShares; i++){
            BigInteger mult3 = yValues[i].multiply(lagrange[i]);
            BigInteger temp7 = mult3.add(recovered);
            recovered = temp7.mod(PRIME);
        }
        return recovered;
    }
}
