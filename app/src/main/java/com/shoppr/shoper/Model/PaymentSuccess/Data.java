package com.shoppr.shoper.Model.PaymentSuccess;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("ref_id")
    @Expose
    private String refId;
    @SerializedName("order_id")
    @Expose
    private Integer orderId;
    @SerializedName("refid")
    @Expose
    private String refid;

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getRefid() {
        return refid;
    }

    public void setRefid(String refid) {
        this.refid = refid;
    }

}