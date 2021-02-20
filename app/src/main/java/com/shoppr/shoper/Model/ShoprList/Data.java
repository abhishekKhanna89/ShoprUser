package com.shoppr.shoper.Model.ShoprList;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("shopper")
    @Expose
    private List<Shopper> shopper = null;


    public List<Shopper> getShopper() {
        return shopper;
    }

    public void setShopper(List<Shopper> shopper) {
        this.shopper = shopper;
    }

}
