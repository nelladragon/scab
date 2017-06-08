// Copyright (C) 2015 Peter Robinson
package com.nelladragon.common.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Load the version of the app from the manifest.
 */
public class AppVersion {

    public static String getVersion(Context c) {
        PackageManager manager = c.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(c.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "9.0";
        }
    }

}
