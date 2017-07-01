package com.nelladragon.scab.store;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sdn on 30/06/17.
 */

public class StoreMain {

    private static StoreMain singleInstance;


    private Context appContext;

    private StoreMain(Context appContext) {
        this.appContext = appContext;
    }


    public static synchronized StoreMain getSingleInstance(Context appContext) {
        if (singleInstance == null) {
            singleInstance = new StoreMain(appContext);
        }
        return singleInstance;
    }


    /**
     * TODO Initially, return everything. This is obviously NOT scalable.
     * @return
     */
    public List<StoreContract> getStoreContracts() {
        List<StoreContract> temp = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            temp.add(new StoreContract(this.appContext, i % 5));
        }


        return temp;
    }



}
