package com.nelladragon.common.util;


/**
 * Created by robinp2 on 18/01/2016.
 */
public class EmailAddressAnalysis {

    public static boolean emailAddressIsValid(String email) {
        if (email == null) {
            // Null email address.
            return false;
        }
        if (email.length() < 6) {
            // Too short: Must be atleast, "a@b.c", which is five characters.
            return false;
        }

        int ofsAt = email.indexOf('@');
        int lastOfsAt = email.lastIndexOf('@');
        int lastOfsDot = email.lastIndexOf('.');

        if (ofsAt == -1) {
            // No @ sign
            return false;
        }
        if (lastOfsAt != ofsAt) {
            // Two or more @ signs
            return false;
        }
        if (lastOfsDot < ofsAt) {
            // There must be a dot after the @ sign.
            return false;
        }
        return true;
    }

}
