package com.shoppr.shoper.activity;

/**
 * Created by AQEEL on 2/25/2019.
 */

public class ChatMessage {


        public String id = "";
        public String sender_id = "";
        public String receiver_id = "";
        public String group_id = "";
        public String send_by = "";
        public String msg_type = "";
        public String message = "";
        public String msg_url = "";
        public String created_at = "";
        public String status = "";

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSender_id() {
            return sender_id;
        }

        public void setSender_id(String sender_id) {
            this.sender_id = sender_id;
        }

        public String getReceiver_id() {
            return receiver_id;
        }

        public void setReceiver_id(String receiver_id) {
            this.receiver_id = receiver_id;
        }

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }

        public String getSend_by() {
            return send_by;
        }

        public void setSend_by(String send_by) {
            this.send_by = send_by;
        }

        public String getMsg_type() {
            return msg_type;
        }

        public void setMsg_type(String msg_type) {
            this.msg_type = msg_type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMsg_url() {
            return msg_url;
        }

        public void setMsg_url(String msg_url) {
            this.msg_url = msg_url;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage_read_status() {
            return message_read_status;
        }

        public void setMessage_read_status(String message_read_status) {
            this.message_read_status = message_read_status;
        }

        public String message_read_status = "";



}
