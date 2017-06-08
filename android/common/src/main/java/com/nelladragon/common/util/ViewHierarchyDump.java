// Copyright (C) 2015 Peter Robinson
package com.nelladragon.common.util;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Dump the hierarchy of the view.
 */
public class ViewHierarchyDump {
    private static final String LOG_TAG = ViewHierarchyDump.class.getSimpleName();


    public static void dump(ViewGroup view) {
        int id = view.getId();
        Log.d(LOG_TAG, "ViewHierarchyDump: id " + i2S(id) + " start");
        List<Integer> ids = new ArrayList<>();
        ids.add(id);
        dumpSubTree(view, ids);
        Log.d(LOG_TAG, "ViewHierarchyDump: id " + i2S(id) + " end");
    }


    private static void dumpSubTree(ViewGroup view, List<Integer> ids) {
        int count = view.getChildCount();
        Log.d(LOG_TAG, idListToString(ids) + ": View Group Child Count: " + count);
        for (int i = 0; i < count; i++) {
            View v = view.getChildAt(i);
            int id = v.getId();
            if (v instanceof ViewGroup) {
                ids.add(id);
                dumpSubTree((ViewGroup) v, ids);
                ids.remove(ids.size()-1);
            } else {
                Log.d(LOG_TAG, idListToString(ids) + ", " + i2S(id) + ": View");
            }
        }

    }


    private static String idListToString(List<Integer> ids) {
        StringBuffer buf = new StringBuffer();
        boolean first = true;
        for (Integer id: ids) {
            if (!first) {
                buf.append(", ");
            }
            first = false;
            buf.append(i2S(id));
        }
        return buf.toString();
    }

    private static String i2S(int i) {
        return "0x" + Integer.toString(i, 16);
    }

}
