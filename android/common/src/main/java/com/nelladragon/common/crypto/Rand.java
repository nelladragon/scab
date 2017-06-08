// Copyright (C) 2015 Peter Robinson
package com.nelladragon.common.crypto;

import java.security.SecureRandom;

/**
 * Pseudo Random Number Generation.
 */
public class Rand {
    // Constants to help with reseed.
    private static final int BYTES_PER_LONG = 8;
    private static final int BYTES_PER_INT = 4;

    // Shared PRNG.
    private static final SecureRandom rand = createRand();

    // Counter which helps with entropy generation.
    private static long count = 0;

    /**
     * Create a shared PRNG.
     *
     * @return An auto seeded PRNG.
     */
    private static SecureRandom createRand() {
        try {
            return SecureRandom.getInstance("SHA1PRNG");
        } catch (Exception e) {
            // No exception should occur. If it does, then let's crash the app...
            // The stack trace will show up in the ADB log.
            // This should only happen during development.
            throw new Error(e);
        }
    }


    /**
     * Generate some random bytes.
     *
     * @param numberOfBytesToGenerate The name says it all.
     * @return Random bytes.
     */
    public static byte[] generateRandom(int numberOfBytesToGenerate) {
        try {
            byte[] randBytes = new byte[numberOfBytesToGenerate];
            addQuickEntropy();
            rand.nextBytes(randBytes);
            return randBytes;
        } catch (Exception e) {
            throw new Error("problem with secure random: " + e.getMessage());
        }
    }


    /**
     * Generate a random int between 0 and MAX INT.
     *
     * @return A random positive integer.
     */
    public static int generateRandomPositiveInt() {
        try {
            addQuickEntropy();
            int val = rand.nextInt();
            if (val < 0) {
                val = -val;
            }
            return val;
        } catch (Exception e) {
            throw new Error("problem with secure random: " + e.getMessage());
        }
    }


    /**
     * Add in additional seeding material to the PRNG.
     *
     * The following is added:
     * * Current time in milli-seconds.
     * * The CPU cycle counter, as returned by nanotime.
     * * The current value of the incrementing counter.
     * * The memory address of a newly created object, as returned by Object.hashCode()
     *
     */
    private static void addQuickEntropy() {
        long time = System.currentTimeMillis();
        long cycleCount = System.nanoTime();
        count = count + cycleCount;
        long countSnapshot = count;
        Object o = new Object();
        int objHashCode = o.hashCode();


        byte[] quickEntropy  = new byte[3 * BYTES_PER_LONG + BYTES_PER_INT];
        int i = 0;
        for (;i < BYTES_PER_LONG; i++) {
            quickEntropy[i] = (byte) time;
            time >>= 8;
        }
        for (;i < 2 * BYTES_PER_LONG; i++) {
            quickEntropy[i] = (byte) cycleCount;
            cycleCount >>= 8;
        }
        for (; i < 3 * BYTES_PER_LONG; i++) {
            quickEntropy[i] = (byte) countSnapshot;
            countSnapshot >>= 8;
        }
        for (; i < 3 * BYTES_PER_LONG + BYTES_PER_INT; i++) {
            quickEntropy[i] = (byte) objHashCode;
            objHashCode >>= 8;
        }

        rand.setSeed(quickEntropy);
    }
}