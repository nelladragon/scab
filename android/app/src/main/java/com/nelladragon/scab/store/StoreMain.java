package com.nelladragon.scab.store;

import android.content.Context;

import com.nelladragon.scab.store.cache.StoreCache;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class to access contract store's cache and cloud services.
 */

public class StoreMain {

    private static StoreMain singleInstance;


    private Context appContext;
    private StoreCache cache;

    private StoreMain(Context appContext) {
        this.appContext = appContext;
        this.cache = new StoreCache(this.appContext);

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
        return this.cache.getAll();
    }




    public StoreContract getContract(String contractId) {
        return new StoreContract(this.appContext);
    }

}
