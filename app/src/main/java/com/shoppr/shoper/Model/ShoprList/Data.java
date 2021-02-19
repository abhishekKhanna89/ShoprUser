package com.shoppr.shoper.Model.ShoprList;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("shopper")
    @Expose
    private List<Shopper> shopper = null;
    @SerializedName("categories")
    @Expose
    private List<Category> categories = null;

    public List<Shopper> getShopper() {
        return shopper;
    }

    public void setShopper(List<Shopper> shopper) {
        this.shopper = shopper;
    }
    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
