package com.shoppr.shoper.Model.InitiatPayment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("payment_done")
    @Expose
    private String paymentDone;

    @SerializedName("refid")
    @Expose
    private String refid;

    @SerializedName("razorpay_order_id")
    @Expose
    private String razorpayOrderId;
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("currency")
    @Expose
    private String currency;




    public String getPaymentDone() {
        return paymentDone;
    }



    public void setPaymentDone(String paymentDone) {
        this.paymentDone = paymentDone;
    }

    public String getRefid() {
        return refid;
    }

    public void setRefid(String refid) {
        this.refid = refid;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

}

