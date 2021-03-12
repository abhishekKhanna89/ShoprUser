package com.shoppr.shoper.Model.ChatMessage;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("chats")
    @Expose
    private List<Chat> chats = null;
    @SerializedName("chat_id")
    @Expose
    private String chatId;
    @SerializedName("items_count")
    @Expose
    private String items_count;
    @SerializedName("shoppr")
    @Expose
    private Shoppr shoppr;

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
    public Shoppr getShoppr() {
        return shoppr;
    }

    public void setShoppr(Shoppr shoppr) {
        this.shoppr = shoppr;
    }

    public String getItems_count() {
        return items_count;
    }

    public void setItems_count(String items_count) {
        this.items_count = items_count;
    }
}
