package com.shoppr.shoper.Model;

public class ChatModel {
    public final static String MSG_TYPE_SENT = "MSG_TYPE_SENT";

    public final static String MSG_TYPE_RECEIVED = "MSG_TYPE_RECEIVED";

    // Message content.
    private String msgContent;

    // Message type.
    private String msgType;
    public ChatModel(String msgType, String msgContent) {
        this.msgType = msgType;
        this.msgContent = msgContent;
    }

    public static String getMsgTypeSent() {
        return MSG_TYPE_SENT;
    }

    public static String getMsgTypeReceived() {
        return MSG_TYPE_RECEIVED;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
}
