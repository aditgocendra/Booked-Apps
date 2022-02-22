package com.ark.bookedapps.Model;

public class ModelOrder {
    private String key_package;
    private String user_id;
    private String date_order;
    private String time_order;
    private String pay_order;
    private String status;
    private String reason_cancel;
    private String key;

    public ModelOrder(){

    }

    public ModelOrder(String key_package, String user_id, String date_order, String time_order, String pay_order, String status, String reason_cancel) {
        this.key_package = key_package;
        this.user_id = user_id;
        this.date_order = date_order;
        this.time_order = time_order;
        this.pay_order = pay_order;
        this.status = status;
        this.reason_cancel = reason_cancel;
    }

    public String getKey_package() {
        return key_package;
    }

    public void setKey_package(String key_package) {
        this.key_package = key_package;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate_order() {
        return date_order;
    }

    public void setDate_order(String date_order) {
        this.date_order = date_order;
    }

    public String getTime_order() {
        return time_order;
    }

    public void setTime_order(String time_order) {
        this.time_order = time_order;
    }

    public String getPay_order() {
        return pay_order;
    }

    public void setPay_order(String pay_order) {
        this.pay_order = pay_order;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason_cancel() {
        return reason_cancel;
    }

    public void setReason_cancel(String reason_cancel) {
        this.reason_cancel = reason_cancel;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
