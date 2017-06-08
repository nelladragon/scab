// Copyright (C) 2015 Peter Robinson
package com.nelladragon.common.inapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Holds persistent application data.
 */
public class DonationDataHolder {
    private static final String TAG = DonationDataHolder.class.getSimpleName();

    private static final String DATA_HOLDER_PREFS = "DONATION_DATA";

    private static final String PREF_DATA_VERSION = "DV";
    private static final int DATA_VERSION_NOT_SET = -1;
    private static final int DATA_VERSION_01 = 1;
    private static final int DATA_VERSION_02 = 2;
    private static final int DATA_VERSION_LATEST = DATA_VERSION_01;
    private int dataVersion;


    private static DonationDataHolder instance;

    private Context appContext;
    private SharedPreferences prefs;

    private DonationDataHolder(Context appContext) {
        this.appContext = appContext;
        this.prefs = this.appContext.getSharedPreferences(DATA_HOLDER_PREFS, Context.MODE_PRIVATE);

        this.dataVersion = this.prefs.getInt(PREF_DATA_VERSION, DATA_VERSION_NOT_SET);
        switch (this.dataVersion) {
            case DATA_VERSION_01:
                break;
            case DATA_VERSION_NOT_SET:
                // App first installed.
                // Fall through.
            default:
                // Unknown version! Wipe everything.
                this.dataVersion = DATA_VERSION_LATEST;
                persist();
                break;
        }

    }


    public static DonationDataHolder getInstance(Context context) {
        if (instance == null) {
            instance = new DonationDataHolder(context.getApplicationContext());
        }
        return  instance;
    }


    public int getDataVersion() {
        return this.dataVersion;
    }



    public void persist() {
        SharedPreferences.Editor editor = this.prefs.edit();
        editor.putInt(PREF_DATA_VERSION, this.dataVersion);
        editor.apply();
    }


    public boolean getDonation(String key) {
        return (this.prefs.getString(key, null) != null);
    }

    public void setDonation(String key) {
        SharedPreferences.Editor editor = this.prefs.edit();
        editor.putString(key, "X");
        editor.apply();
    }
    public void removeDonation(String key) {
        SharedPreferences.Editor editor = this.prefs.edit();
        editor.remove(key);
        editor.apply();
    }
}
