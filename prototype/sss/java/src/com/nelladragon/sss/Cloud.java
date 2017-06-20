package com.nelladragon.sss;

import java.util.HashMap;
import java.util.Map;

/**
 * This class emulates the APIs offered by the cloud and what is stored in the cloud.
 */
public class Cloud {

    // Types of data stored in the cloud.
    public static final int PASSWORD_HARDENING_PARAMETERS = 0;
    public static final int BACKUPPASSWORD_HARDENING_PARAMETERS = 1;
    public static final int DEVICE_DATA = 2;
    public static final int DEVICE_DATA_PARAMETERS = 3;

    public static final int CLOUD_SHARE1 = 4;
    public static final int CLOUD_SHARE2 = 5;
    public static final int CLOUD_SHARE3 = 6;
    public static final int CLOUD_SHARE4 = 7;


    private static Map<Integer, byte[]> cloudData = new HashMap<>();


    private static Cloud theCloud;

    public static synchronized Cloud getSingleInstance() {
        if (theCloud == null) {
            theCloud = new Cloud();
        }
        return theCloud;
    }

    public void setData(int type, byte[] data) {
        cloudData.put(type, data);
    }


    //TODO need to do something smart about versioning of data / locking to avoid problems with partial changes.

    public void setDataMultiple(int[] type, byte[][] data) {
        if (type.length != data.length) {
            throw new RuntimeException("Type and data arrays are different lengths");
        }
        for (int i=0; i < type.length; i++) {
            cloudData.put(type[i], data[i]);
        }
    }


    public byte[] getData(int type) {
        return cloudData.get(type);
    }


}
