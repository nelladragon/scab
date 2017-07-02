package com.nelladragon.scab.store.cache;

import android.content.Context;

import com.nelladragon.scab.store.ContractId;
import com.nelladragon.scab.store.StoreContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Cache on device of contracts.
 */

public class StoreCache {

    private Context appContext;

    private Map<String, StoreContract> inMemoryCache = new TreeMap<>();
    private boolean inMemoryCacheLoaded = false;



    public StoreCache(Context appContext) {
        this.appContext = appContext;

    }



    public StoreContract isInCache(ContractId id) {
        if (!this.inMemoryCacheLoaded) {
            loadDiskCache();
            this.inMemoryCacheLoaded = true;
        }
        return this.inMemoryCache.get(id);
    }


    // TODO: Should there be a check for whether old being added over new version?
    // TODO: Need to add a check to ensure not too many contracts are in cache / to limit cache growth

    public void addToCache(StoreContract contract) {
        String id = contract.getId().getId();
        this.inMemoryCache.put(id, contract);
        saveToDiskCache(contract);
    }


    public List<StoreContract> getAll() {


        // TODO
        List<StoreContract> temp = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            temp.add(new StoreContract(this.appContext));
        }


        return temp;

    }



    private void loadDiskCache() {
        // TODO load hard coded data for the moment.
    }

    private void saveToDiskCache(StoreContract contract) {
        // TODO do nothing for the moment.
    }


}
