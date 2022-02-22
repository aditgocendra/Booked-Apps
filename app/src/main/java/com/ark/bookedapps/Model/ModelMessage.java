package com.ark.bookedapps.Model;

public class ModelMessage {

    private String uidSender;
    private String message;
    private String timeStamp;
    private boolean read;
    private String key;


    public ModelMessage(){

    }

    public ModelMessage(String uidSender, String message, String timeStamp, boolean read) {
        this.uidSender = uidSender;
        this.message = message;
        this.timeStamp = timeStamp;
        this.read = read;
    }

    public String getUidSender() {
        return uidSender;
    }

    public void setUidSender(String uidSender) {
        this.uidSender = uidSender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
