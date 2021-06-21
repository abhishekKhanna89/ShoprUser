package com.shoppr.shoper.Model.CartView;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("items")
    @Expose
    private List<Item> items = null;
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("service_charge")
    @Expose
    private Integer serviceCharge;
    @SerializedName("grand_total")
    @Expose
    private Integer grandTotal;
    @SerializedName("wallet_balance")
    @Expose
    private Integer wallet_balance;

    @SerializedName("discount_amount")
    @Expose
    private Integer discountamount;
    public Integer getDiscount() {
        return discountamount;
    }

    public void setDiscount(Integer discountamount) {
        this.discountamount = discountamount;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(Integer serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public Integer getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Integer grandTotal) {
        this.grandTotal = grandTotal;
    }

    public Integer getWallet_balance() {
        return wallet_balance;
    }

    public void setWallet_balance(Integer wallet_balance) {
        this.wallet_balance = wallet_balance;
    }
}
