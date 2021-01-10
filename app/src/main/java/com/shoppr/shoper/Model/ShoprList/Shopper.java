package com.shoppr.shoper.Model.ShoprList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Shopper {

    @SerializedName("shoppr_count")
    @Expose
    private Integer shopprCount;
    @SerializedName("location")
    @Expose
    private String location;

    public Integer getShopprCount() {
        return shopprCount;
    }

    public void setShopprCount(Integer shopprCount) {
        this.shopprCount = shopprCount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
