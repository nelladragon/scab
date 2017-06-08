// Copyright (C) 2015 Peter Robinson
package com.nelladragon.showertimertalking.users;

import android.content.Context;

import com.nelladragon.showertimertalking.R;
import com.nelladragon.util.crypto.Rand;

/**
 * Utilities needed for loading photos.
 */
public class UserPhotoUtil {

    public static final int PHOTO_SIZE_DP = 48;

    public static final int DRAWABLE_FACEBOOK = 0;
    public static final int DRAWABLE_GOOGLE = 1;

    public static final int DRAWABLE_USER_SELECTED_MIN = 1000;

    /**
     * String drawable name to resource id.
     *
     * @param c
     * @param drawableName
     * @return
     */
    public static int drawableNameToId(Context c, String drawableName) {
        return c.getResources().getIdentifier(drawableName, "drawable", c.getPackageName());
    }


    public static final int PHOTO01 = 101;
    public static final int PHOTO02 = 102;
    public static final int PHOTO03 = 103;
    public static final int PHOTO04 = 104;
    public static final int PHOTO05 = 105;
    public static final int PHOTO06 = 106;
    public static final int DEFAULT_ID = PHOTO06;

    private static final String user_icon_1 = "user_icon_1";
    private static final String user_icon_2 = "user_icon_2";
    private static final String user_icon_3 = "user_icon_3";
    private static final String user_icon_4 = "user_icon_4";
    private static final String user_icon_5 = "user_icon_5";
    private static final String user_icon_6 = "user_icon_6";
    public static final String DEFAULT_STR = user_icon_6;


    public static int drawableNameToPhotoId(String drawableName) {
        if (drawableName.equals(user_icon_1)) {
            return PHOTO01;
        }
        if (drawableName.equals(user_icon_2)) {
            return PHOTO02;
        }
        if (drawableName.equals(user_icon_3)) {
            return PHOTO03;
        }
        if (drawableName.equals(user_icon_4)) {
            return PHOTO04;
        }
        if (drawableName.equals(user_icon_5)) {
            return PHOTO05;
        }
        if (drawableName.equals(user_icon_6)) {
            return PHOTO06;
        }
        // If the drawable can't be found, default to the flower photo.
        return DEFAULT_ID;
    }

    public static String photoidToDrawableName(int id) {
        switch (id) {
            case PHOTO01:
                return user_icon_1;
            case PHOTO02:
                return user_icon_2;
            case PHOTO03:
                return user_icon_3;
            case PHOTO04:
                return user_icon_4;
            case PHOTO05:
                return user_icon_5;
            case PHOTO06:
                return user_icon_6;
            default:
                return DEFAULT_STR;
        }
    }

    public static int photoIdToDrawableId(int id) {
        switch (id) {
            case PHOTO01:
                return R.drawable.user_icon_1;
            case PHOTO02:
                return R.drawable.user_icon_2;
            case PHOTO03:
                return R.drawable.user_icon_3;
            case PHOTO04:
                return R.drawable.user_icon_4;
            case PHOTO05:
                return R.drawable.user_icon_5;
            case PHOTO06:
                return R.drawable.user_icon_6;
            default:
                return R.drawable.user_icon_6;
        }
    }


    /**
     * I should look for duplicates, but, assuming a couple of photos, it is very
     * unlikely we will have duplicates.
     *
     * @return
     */
    public static int getRandomUserPhotoId() {
        int id;
        do {
            id = Rand.generateRandomPositiveInt();
        } while (id < DRAWABLE_USER_SELECTED_MIN);
        return id;
    }

    public static boolean isUserPhotoId(int photoId) {
        return photoId >= DRAWABLE_USER_SELECTED_MIN;
    }

}
