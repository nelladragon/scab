// Copyright (c) 2015 Peter Robinson
package com.nelladragon.common.inapp;

import android.content.Context;

import com.nelladragon.common.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Cache of what in-app purchases have occurred. Having this local
 * cache allows the user to see what donations they have made, even
 * when they are off-line.
 */
public class DonationCache {
    private static final int DEFAULT_AMOUT = 1;


    private static final String SKU_DONATION_0001 = "donation_0001";
    private static final String SKU_DONATION_0002 = "donation_0002";
    private static final String SKU_DONATION_0003 = "donation_0003";
    private static final String SKU_DONATION_0005 = "donation_0005";
    private static final String SKU_DONATION_0010 = "donation_0010";
    private static final String SKU_DONATION_0020 = "donation_0020";
    private static final String SKU_DONATION_0030 = "donation_0030";
    private static final String SKU_DONATION_0050 = "donation_0050";

    private static final int DESC_DONATION_0001 = R.string.donate_0001;
    private static final int DESC_DONATION_0002 = R.string.donate_0002;
    private static final int DESC_DONATION_0003 = R.string.donate_0003;
    private static final int DESC_DONATION_0005 = R.string.donate_0005;
    private static final int DESC_DONATION_0010 = R.string.donate_0010;
    private static final int DESC_DONATION_0020 = R.string.donate_0020;
    private static final int DESC_DONATION_0030 = R.string.donate_0030;
    private static final int DESC_DONATION_0050 = R.string.donate_0050;

    private static final int DONATION_0001 = 1;
    private static final int DONATION_0002 = 2;
    private static final int DONATION_0003 = 3;
    private static final int DONATION_0005 = 5;
    private static final int DONATION_0010 = 10;
    private static final int DONATION_0020 = 20;
    private static final int DONATION_0030 = 30;
    private static final int DONATION_0050 = 50;



    Context appContext;
    DonationDataHolder data;
    List<DonateItem> cache;

    private static DonationCache instance;

    public static class DonateItem {
        public int description;
        public String skuName;
        public int amount;
        public boolean purchased;

        /**
         *
         * @param description Resource Id of text describing purchase.
         * @param skuName SKU name in Android developer console.
         * @param amount Short form dollar amount used for debug.
         * @param purchased Whether item has been purchased.
         */
        public DonateItem(int description, String skuName, int amount, boolean purchased) {
            this.description = description;
            this.skuName = skuName;
            this.amount = amount;
            this.purchased = purchased;
        }
    }

    private DonationCache(Context appContext) {
        this.appContext = appContext;
        this.data = DonationDataHolder.getInstance(this.appContext);
        this.cache = loadDonationInfoInternal();
    }

    public static DonationCache getInstance(Context context) {
        if (instance == null) {
            instance = new DonationCache(context.getApplicationContext());
        }
        return instance;
    }




    public List<DonateItem> loadDonationInfoInternal() {
        List<DonateItem> items = new ArrayList<>();
        items.add(load(DESC_DONATION_0001, SKU_DONATION_0001, DONATION_0001));
        items.add(load(DESC_DONATION_0002, SKU_DONATION_0002, DONATION_0002));
        items.add(load(DESC_DONATION_0003, SKU_DONATION_0003, DONATION_0003));
        items.add(load(DESC_DONATION_0005, SKU_DONATION_0005, DONATION_0005));
        items.add(load(DESC_DONATION_0010, SKU_DONATION_0010, DONATION_0010));
        items.add(load(DESC_DONATION_0020, SKU_DONATION_0020, DONATION_0020));
        items.add(load(DESC_DONATION_0030, SKU_DONATION_0030, DONATION_0030));
        items.add(load(DESC_DONATION_0050, SKU_DONATION_0050, DONATION_0050));
        return items;
    }

    private DonateItem load(int desc, String sku, int shortForm) {
        boolean purchased = this.data.getDonation(sku);
        return new DonateItem(desc, sku, shortForm, purchased);
    }


    public List<DonateItem> loadDonationInfo() {
        return this.cache;
    }


    public void updateBasedOnInventory(Inventory inventory) {
        for (DonateItem item: this.cache) {
            item.purchased = inventory.hasPurchase(item.skuName);
            if (item.purchased) {
                this.data.setDonation(item.skuName);
            } else {
                this.data.removeDonation(item.skuName);
            }
        }
    }

    public void updateBasedOnPurchase(Purchase purchase) {
        String sku = purchase.getSku();
        for (DonateItem item: this.cache) {
            if (item.skuName.equals(sku)) {
                item.purchased = true;
                data.setDonation(item.skuName);
                break;
            }
        }
    }

    public int getAmountBasedOnSku(String sku) {
        for (DonateItem item: this.cache) {
            if (item.skuName.equals(sku)) {
                return item.amount;
            }
        }
        return DEFAULT_AMOUT;
    }
}
