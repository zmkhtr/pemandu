package com.sahabatpnj.pemandu.model;

public class ChatMessage {
    private String message;
    private String senderId;
    private String receiverId;
    private String messageId;

    public ChatMessage() {
    }

    public ChatMessage(String message, String senderId, String receiverId, String messageId) {
        this.message = message;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageR) {
        this.messageId = messageR;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}