package com.nelladragon.scab.store;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.content.ContextCompat;

import com.nelladragon.scab.users.UserPhotoUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Smart Contract Application which is in the store.
 */

public class StoreContract {
    // The maximum length of a contract name.
    public static final int CONTRACT_NAME_MAX_LENGTH = 30;


    private List<StoreCategory> categories = new ArrayList<>();

    private Context appContext;

    private String contractName;


    private ContractId id;

    public StoreContract(Context appContext) {
        this.appContext = appContext;
        this.categories.add(StoreCategory.UNCATEGORIZED);

        // TODO the contract would have an id generated when it was deployed.
        this.id = ContractId.generateRandomContractId();


        String originalContractName = "Some Contract App " + this.id;
        this.contractName= fixContractName(originalContractName);
    }


    /**
     * Limit the length of contract names. Limiting the length of
     * contract names is done here, as well as when the contract is
     * being authored, to ensure an attacker can't hack the GUI of the
     * app such that it doesn't display correctly.
     *
     * @param originalName Name from the contract package.
     * @return Processed name.
     */
    private static String fixContractName(String originalName){
        return originalName.substring(0, CONTRACT_NAME_MAX_LENGTH);
    }



    public List<StoreCategory> getCategories() {
        return this.categories;
    }


    /**
     * Get the image to be used in the list for the store.
     *
     * @return
     */
    public Drawable getStoreListImage() {

        int drawableId = UserPhotoUtilTemp.photoIdToDrawableId(101);
        return ContextCompat.getDrawable(this.appContext, drawableId);
    }


    public String getName() {
        return this.contractName;
    }

    public String getStoreListDescription() {
        return "A detailed description about some contract app. " + this.id;
    }


    public ContractId getId() {
        return this.id;
    }





}
