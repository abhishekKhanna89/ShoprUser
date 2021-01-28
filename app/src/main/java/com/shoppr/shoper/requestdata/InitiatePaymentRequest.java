package com.shoppr.shoper.requestdata;

public class InitiatePaymentRequest {
    String type;
    int use_balance;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUse_balance() {
        return use_balance;
    }

    public void setUse_balance(int use_balance) {
        this.use_balance = use_balance;
    }
}
