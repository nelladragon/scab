// Copyright (C) 2017 Peter Robinson
package com.nelladragon.sss.split;

import java.math.BigInteger;

import com.nelladragon.sss.util.*;

import java.security.SecureRandom;


/**
 * Threshold scheme which is similar to Shamir's scheme.
 *
 * The difference is that there is a fixed share. The fixed share always
 * has an X value of 1.
 */
public final class FixedShareThresholdScheme {

    /**
     * Prevent construction. All methods in this class are static.
     */
    private FixedShareThresholdScheme() {
    }


    /**
     * Generate the coefficients used to construct the polynomial randomly.
     * <p>
     * The coefficients will be length(p)-1 bits long and they will have length(p)-2 bits of
     * randomness where the most significant bit is always 1. The maximum value will be p-1.
     *
     * @param p The prime modulus.
     * @param threshold The number of shares needed to recover the secret.
     * @param random The random used to generate the coefficients.
     * @return An array of <code>MPInteger</code>s containing the coefficients.
     */
    public static BigInteger[] generateRandomCoefficients(BigInteger p, int threshold, SecureRandom random) {
        int numCoefficients = threshold - 1;
        BigInteger[] coefficients = new BigInteger[numCoefficients];
        int pLength = p.bitLength();
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
        BigInteger offset = p.subtract(maxCandidate);
        offset = offset.subtract(BigInteger.ONE);

        for(int i = 0; i < numCoefficients; i++){
            // Generate a random coefficient.
            random.nextBytes(coefficientData);
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
     * Generate coefficients to generate a random polynomial, but keep the
     * secret secret and the password share values fixed.
     *
     * @param p Prime modulus.
     * @param threshold The number of shares needed to recover the secret.
     * @param random A PRNG.
     * @return Coefficients to be used to generate shares for proactivization.
     */
    public static BigInteger[] generateProactivizationCoefficients(
            BigInteger p, int threshold, SecureRandom random) {
        // For proactivization we are keeping the secret and the password share static.
        // That is, for the x values corresponding to the password share and the
        // secret value, the y value should be 0.

        // Imagine: y = a*x**3 + b*x**2 + c*x + d
        // For secret, x=0, we want y=0, hence d=0.
        // For password share, x=1, we want y=0, hence, c = -(a + b)

        // Randomly generate all but one of the coefficients.
        BigInteger[] randomCoefs = generateRandomCoefficients(p, threshold - 1, random);
        // Calculate the final coefficient.
        BigInteger derivedCoef = BigInteger.ZERO;
        for (BigInteger randomCoef: randomCoefs) {
            derivedCoef = derivedCoef.subtract(randomCoef);
            derivedCoef = derivedCoef.mod(p);
        }
        // Combine the derived and the fixed coefficients.
        BigInteger[] coefs = new BigInteger[randomCoefs.length + 1];
        for (int i = 0; i < randomCoefs.length; i++) {
            coefs[i] = randomCoefs[i];
        }
        coefs[coefs.length - 1] = derivedCoef;
        return  coefs;
    }



    /**
     * Generate coefficients to generate a random polynomial, but keep the
     * secret secret value fixed and change the password by a known amount.
     *
     * @param p Prime modulus.
     * @param threshold The number of shares needed to recover the secret.
     * @param random A PRNG.
     * @return Coefficients to be used to generate shares for proactivization.
     */
    public static BigInteger[] generatePasswordChangeCoefficients(
            BigInteger p, int threshold, SecureRandom random, BigInteger passwordChangeValue) {
        // For password change we are keeping the secret static,
        // and changing the password by the passwordChangeValue.
        // That is: for the x value corresponding to secret value, the y value should be 0.
        // That is: for the x value corresponding to password share, the y value should be passwordChangeValue.

        // Imagine: y = a*x**3 + b*x**2 + c*x + d
        // For secret, x=0, we want y=0, hence d=0.
        // For password share, x=1, we want y=passwordChangeValue, hence, c = passwordChangeValue - (a + b)

        // Randomly generate all but one of the coefficients.
        BigInteger[] randomCoefs = generateRandomCoefficients(p, threshold - 1, random);
        // Calculate the final coefficient.
        BigInteger derivedCoef = passwordChangeValue;
        for (BigInteger randomCoef: randomCoefs) {
            derivedCoef = derivedCoef.subtract(randomCoef);
            derivedCoef = derivedCoef.mod(p);
        }
        // Combine the derived and the fixed coefficients.
        BigInteger[] coefs = new BigInteger[randomCoefs.length + 1];
        for (int i = 0; i < randomCoefs.length; i++) {
            coefs[i] = randomCoefs[i];
        }
        coefs[coefs.length - 1] = derivedCoef;
        return  coefs;
    }




    /**
     * Randomly generate the secret value (the secret) and the Y values for all X values,
     * given the fixed password secret.
     *
     * @param p Prime modulus.
     * @param passwordSecret Secret value for x=1.
     * @param xValues X values to generate y values for.
     * @param coefficients The coefficients of the polynomial. All the values should be smaller than p.
     *                     There should be threshold-1 coefficients.
     * @return Y values to match the X values.
     */
    public static BigInteger[] generateSharesGivenPasswordShare(
            final BigInteger p, final BigInteger passwordSecret, final BigInteger[] xValues,
            final BigInteger[] coefficients) {
        // Calculate the secret value such that the y value for x=1 is equal to the password secret.
        // y = a * x**2 + b * x + c
        // For x = 1
        // y(1) = a + b + c
        // Hence:
        // passwordSecret = a + b + secret
        // secret = passwordSecret - a - b
        BigInteger accumulator = passwordSecret;
        for (int i = 0; i < coefficients.length; i++) {
            accumulator = accumulator.subtract(coefficients[i]);
            accumulator = accumulator.mod(p);
        }
        BigInteger secret = accumulator;
        BigInteger[] yValues = generateShares(p, secret, xValues, coefficients);
        BigInteger[] yValuesAndSecret = new BigInteger[yValues.length + 1];
        yValuesAndSecret[0] = secret;
        for (int i = 0; i < yValues.length; i++) {
            yValuesAndSecret[i+1] = yValues[i];
        }
        return yValuesAndSecret;
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
     * @param p The prime modulus.
     * @param secret The value to protect. This will be the x=0 value.
     * @param xValues The x coordinates of the shares. Note that none of them should be zero.
     *  All xValues should be unique.
     * @param coefficients The coefficients of the polynomial. All the values should be smaller than p.
     *          There should be threshold-1 coefficients.
     * @return The y coordinates of the shares.
     */
    public static BigInteger[] generateShares(BigInteger p, BigInteger secret, BigInteger[] xValues,
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
                BigInteger exp = xValues[i].modPow(k, p);
                BigInteger mult = exp.multiply(coefficients[j]);
                k = k.add(BigInteger.ONE);

                // Accumulate the term
                BigInteger add1 = accumulator.add(mult);
                accumulator = add1.mod(p);
            }
            // Accumulate the constant term; which is the secret.
            BigInteger add2 = accumulator.add(secret);
            accumulator = add2.mod(p);

            yVals[i] = accumulator;
        }

        return yVals;
    }


    /**
     * Generate a set of random X values mod p.
     *
     * @param p Prime modulus.
     * @param numberOfShares Number of X values to generate.
     * @param random A PRNG.
     * @return The array of random X values.
     */
    public static BigInteger[] generateXvalues(BigInteger p, int numberOfShares, SecureRandom random) {
        BigInteger[] xValues = new BigInteger[numberOfShares];
        for (int i = 0; i < xValues.length; i++) {
            xValues[i] = generateXval(p, xValues, i, random);
        }
        return xValues;

    }

    /**
     * Generate a single random X value mod p. The X value can not be 0 (maps to the secret secret
     * value), 1 (maps to the fixed password share value), or any X value which has already been used.
     *
     * @param p Prime modulus.
     * @param existingXValues Array containing the values already in use.
     * @param numXvals Number of values int he existingXValues array which are in use.
     * @param random A PRNG.
     * @return A random X value.
     */
    public static BigInteger generateXval(BigInteger p, BigInteger[] existingXValues, int numXvals, SecureRandom random) {
        boolean valid = true;
        BigInteger candidate;
        do {
            byte[] xValueBytes = new byte[256];
            random.nextBytes(xValueBytes);
            candidate = new BigInteger(xValueBytes);
            candidate = candidate.mod(p);

            // X values can not be zero (the secret secret value), or one (the fixed password share value)
            if (candidate.equals(BigInteger.ZERO) || candidate.equals(BigInteger.ONE)) {
                valid = false;
            }
            // Can not use the same X value twice.
            for (int j = 0; j < numXvals; j++) {
                if (candidate.equals(existingXValues[j])) {
                    valid = false;
                }
            }
        } while (!valid);
        return candidate;
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
     * @param p Prime modulus.
     * @param xValues X coordinates of the shares.
     * @param yValues Y coordinates of the shares.
     * @param x The x value. If x=0 then the secret is recovered, otherwise a new share is created.
     * @return The recovered secret.
     */
    public static BigInteger recoverSecret(BigInteger p, BigInteger[] xValues, BigInteger[] yValues, BigInteger x) {
        int numShares = xValues.length;

        // Compute the Lagrange basis polynomials L0 to Ln-1.
        BigInteger[] lagrange = new BigInteger[numShares];
        for(int i = 0; i < numShares; i++){
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;
            for(int j = 0; j < numShares; j++){
                if(j != i){
                    // Numerator = (x-x0) * (x-x1) * (x-x2) * ...
                    BigInteger temp1 = x.subtract(xValues[j]);
                    BigInteger temp2 = temp1.mod(p);
                    BigInteger mult1 = numerator.multiply(temp2);
                    numerator = mult1.mod(p);

                    // Denominator = (xi-x0) * (xi-x1) * (xi-x2) * ...
                    BigInteger temp3 = xValues[i].subtract(xValues[j]);
                    BigInteger temp4 = temp3.mod(p);
                    BigInteger mult2 = denominator.multiply(temp4);
                    denominator = mult2.mod(p);
                }
            }

            // Li = numerator / denominator
            BigInteger temp5 = denominator.modInverse(p);
            BigInteger temp6 = numerator.multiply(temp5);
            lagrange[i] = temp6.mod(p);
        }

        // recovered = (y0 * L0) + (y1 * L1) +...
        BigInteger recovered = BigInteger.ZERO;
        for(int i=0; i < numShares; i++){
            BigInteger mult3 = yValues[i].multiply(lagrange[i]);
            BigInteger temp7 = mult3.add(recovered);
            recovered = temp7.mod(p);
        }
        return recovered;
    }
}
