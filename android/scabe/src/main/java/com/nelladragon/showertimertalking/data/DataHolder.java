// Copyright (C) 2015 Peter Robinson
package com.nelladragon.showertimertalking.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;

import com.nelladragon.showertimertalking.users.UserPhotoUtil;

import java.io.ByteArrayOutputStream;

/**
 * Holds persistent application data.
 */
public class DataHolder {
    private static final String TAG = DataHolder.class.getSimpleName();

    private static final String DATA_HOLDER_PREFS = "DATA_HOLDER";

    private static final String PREF_DATA_VERSION = "DV";
    private static final int DATA_VERSION_NOT_SET = -1;
    private static final int DATA_VERSION_01 = 1;
    private static final int DATA_VERSION_02 = 2;
    private static final int DATA_VERSION_LATEST = DATA_VERSION_02;
    private int dataVersion;

    private static final String PREF_CURRENT = "CUR";
    private String currentUser = null;


    private static DataHolder instance;

    private Context appContext;
    private SharedPreferences prefs;

    private boolean first = false;

    private DataHolder(Context appContext) {
        this.appContext = appContext;
        this.prefs = this.appContext.getSharedPreferences(DATA_HOLDER_PREFS, Context.MODE_PRIVATE);

        final String facebookNameDefault = "*****";

        this.dataVersion = this.prefs.getInt(PREF_DATA_VERSION, DATA_VERSION_NOT_SET);
        switch (this.dataVersion) {
            case DATA_VERSION_01:
            case DATA_VERSION_02:
                this.currentUser = this.prefs.getString(PREF_CURRENT, null);
                break;
            case DATA_VERSION_NOT_SET:
                // App first installed.
                // Fall through.
                first = true;
            default:
                // Unknown version! Wipe everything.
                this.dataVersion = DATA_VERSION_LATEST;
                this.currentUser = null;
                persist();
                break;
        }

    }


    public static DataHolder getInstance(Context context) {
        if (instance == null) {
            instance = new DataHolder(context.getApplicationContext());
        }
        return  instance;
    }

    /**
     *
     * @return true the first time called, when the app has first been installed.
     */
    public boolean isFirstTimeRun() {
        boolean ret = this.first;
        this.first = false;
        return ret;
    }


    public int getDataVersion() {
        return this.dataVersion;
    }


    public void setCurrent(String cur) {
        this.currentUser = cur;
    }
    public String getCurrent() {
        return this.currentUser;
    }

    public void persist() {
        SharedPreferences.Editor editor = this.prefs.edit();
        editor.putInt(PREF_DATA_VERSION, this.dataVersion);

        if (this.currentUser == null) {
            editor.remove(PREF_CURRENT);
        } else {
            editor.putString(PREF_CURRENT, this.currentUser);
        }
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


    public Drawable getUserPhoto(int photoId) {
        try {
            String photoB64 =  this.prefs.getString(photoIdToPrefKey(photoId), null);
            if (photoB64 == null) {
                Log.e(TAG, "PhotoId not found in data holder: " + photoId);
                return null;
            }

            byte[] image = Base64.decode(photoB64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            return new BitmapDrawable(this.appContext.getResources(), bitmap);
        } catch (Exception e) {
            return null;
        }
    }

    public void setUserPhoto(int photoId, Drawable photo) {
        if (photo == null) {
            Log.e(TAG, "Storing null photoId: " + photoId);
            return;
        }
        Log.d(TAG, "Storing photoId: " + photoId);

        Bitmap bitmap = ((BitmapDrawable) photo).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.setHasAlpha(true);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] image = baos.toByteArray();
        String userPhoto = Base64.encodeToString(image, Base64.DEFAULT);

        SharedPreferences.Editor editor = this.prefs.edit();
        editor.putString(photoIdToPrefKey(photoId), userPhoto);
        editor.apply();
    }
    public void deleteUserPhoto(int photoId) {
        SharedPreferences.Editor editor = this.prefs.edit();
        editor.remove(photoIdToPrefKey(photoId));
        editor.apply();
    }


    private String photoIdToPrefKey(int photoId) {
        return "USER_" + photoId;
    }



}
