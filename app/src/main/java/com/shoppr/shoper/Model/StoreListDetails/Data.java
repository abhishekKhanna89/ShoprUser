package com.shoppr.shoper.Model.StoreListDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("stores_details")
    @Expose
    private StoresDetails storesDetails;

    public StoresDetails getStoresDetails() {
        return storesDetails;
    }

    public void setStoresDetails(StoresDetails storesDetails) {
        this.storesDetails = storesDetails;
    }

}