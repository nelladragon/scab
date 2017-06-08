// Copyright (c) 2015 Peter Robinson
package com.nelladragon.common.inapp;

import com.nelladragon.common.CommonLib;

/**
 * Create the in-app purchasing verification key. Google indicate that this
 * public key needs to be kept securely. This doesn't make a lot of sense given
 * it is an RSA public key.
 *
 * Shower Timer Talking's key is:
 * "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw/9YKlFxlgo9L3h1JcTz7m5NGxqGS0IVyAFZlnT721mgIYW8zRtdNvCSxNmdVdWCbU1QJq4c6W/YlHer18p4vedq3PWSGvo0zcDKloNQ0BVaStkXJfTYCpuT7haOGKI+GeZ5pf35EvyKKoXkNY9BxBFpL+RMrwmqVsazmnoMlAUF9Jnsv4uNJaSAqeWRjq1cUM9jPqzWgo1pVFcd24P+29RgNr5KCCu7BiycyEsU8rn1OkFXbai2KwrsJu10UR8436pc+G+eJD7igPqfFfOpuGFogc46JKrHFMk7aexEEAaAm5Hv+L84IaeL+kz8bIiXCgdMiues4n9lk7KF7P5rSwIDAQAB";
 */
public class IabPubKey {
    /**
     * In App Billing public key, very slightly obfuscated.
     */
    public static String construct() {
        CommonLib lib = CommonLib.getInstance();
        return lib.getInAppBillPubkey();
    }
}


