package com.nelladragon.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Checks internet connectivity.
 *
 */
public class NetworkConnectionStatus {

    /**
     * Checks connectivity to the internet.
     *
     * Note that the following must be in the manifest for this to work:
     *
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     *
     * @param context Any context.
     * @return true if the internet is available.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
