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


    private List<StoreCategory> categories = new ArrayList<>();

    private Context appContext;


    private int id;

    public StoreContract(Context appContext, int temp) {
        this.appContext = appContext;
        this.categories.add(StoreCategory.UNCATEGORIZED);
        this.id = temp;
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

        int drawableId = UserPhotoUtilTemp.photoIdToDrawableId(this.id);
        return ContextCompat.getDrawable(this.appContext, drawableId);
    }


    public String getName() {
        return "Some Contract App" + this.id;
    }

    public String getStoreListDescription() {
        return "A detailed description about some contract app." + this.id;
    }




}
