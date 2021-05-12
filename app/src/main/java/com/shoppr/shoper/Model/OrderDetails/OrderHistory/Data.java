package com.shoppr.shoper.Model.OrderDetails.OrderHistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("order")
    @Expose
    private Order order;
    @SerializedName("show_invoice_link")
    @Expose
    private String show_invoice_link;
    @SerializedName("payment_text")
    @Expose
    private String payment_text;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getShow_invoice_link() {
        return show_invoice_link;
    }

    public void setShow_invoice_link(String show_invoice_link) {
        this.show_invoice_link = show_invoice_link;
    }

    public String getPayment_text() {
        return payment_text;
    }

    public void setPayment_text(String payment_text) {
        this.payment_text = payment_text;
    }
}
