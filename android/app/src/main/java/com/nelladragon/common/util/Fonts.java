// Copyright (C) 2015 Peter Robinson
package com.nelladragon.common.util;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Hold all fonts statically. It causes more memory to be used, but saves battery
 * as they don't need to be reloaded.
 */
public class Fonts {

    private static Typeface degaws;
    private static Typeface chantelli;
    private static Typeface letsGoDigital;
    private static Typeface handlee;

    private static Context appContext;

    public static Typeface getDegaws(Context context) {
        if (appContext == null) {
            appContext = context.getApplicationContext();
        }
        if (degaws == null) {
            degaws = Typeface.createFromAsset(appContext.getAssets(), "DEGAWS Demo.ttf");
        }
        return degaws;

    }

    public static Typeface getChantelli(Context context) {
        if (appContext == null) {
            appContext = context.getApplicationContext();
        }
        if (chantelli == null) {
            chantelli = Typeface.createFromAsset(appContext.getAssets(), "Chantelli_Antiqua.ttf");
        }
        return chantelli;
    }

    public static Typeface getLetsGoDigital(Context context) {
        if (appContext == null) {
            appContext = context.getApplicationContext();
        }
        if (letsGoDigital == null) {
            letsGoDigital = Typeface.createFromAsset(appContext.getAssets(), "lets_go_digital.ttf");
        }
        return letsGoDigital;
    }


    public static Typeface getHandlee(Context context) {
        if (appContext == null) {
            appContext = context.getApplicationContext();
        }
        if (handlee == null) {
            handlee = Typeface.createFromAsset(appContext.getAssets(), "Handlee-Regular.ttf");
        }
        return handlee;
    }
}
