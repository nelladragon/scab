// Copyright (c) 2015 Peter Robinson
package com.nelladragon.scab.guistate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.widget.Chronometer;

/**
 * Control the chronometer and store state to allow for screen orientation change.
 */
public class ChronometerControl {
    private static String PREFS_NAME = "CHRON";
    private static String PREFS_VALUE = "VAL";

    private Chronometer chronometer;
    private Context context;
    private long base;

    public ChronometerControl(Context context, Chronometer c) {
        this.chronometer = c;
        this.context = context;
    }


    // Initialize with zero.
    public void initStateAndStart() {
        this.base = SystemClock.elapsedRealtime();
        this.chronometer.setBase(SystemClock.elapsedRealtime());
        this.chronometer.start();
        deleteState();
    }

    // Store the different between the start time and now.
    public void storeState() {
        SharedPreferences settings = this.context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        long duration = SystemClock.elapsedRealtime() - this.base;

        editor.putLong(PREFS_VALUE, duration);

        editor.commit();
    }

    // Set the Chronometer to start from the duration so far.
    public void restoreState() {
        SharedPreferences settings = this.context.getSharedPreferences(PREFS_NAME, 0);
        long duration = settings.getLong(PREFS_VALUE, 0);

        this.base = SystemClock.elapsedRealtime() - duration;
        this.chronometer.setBase(this.base);
    }

    public void start() {
        this.chronometer.start();
    }


    public void deleteState() {
        SharedPreferences settings = this.context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(PREFS_VALUE);
        editor.apply();
    }


    public long getElapsedTimeInMs() {
        return SystemClock.elapsedRealtime() - this.base;
    }

}
