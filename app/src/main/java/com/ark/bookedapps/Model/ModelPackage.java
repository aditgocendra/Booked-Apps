package com.ark.bookedapps.Model;

public class ModelPackage {
    private String package_name;
    private String price;
    private String detail;
    private String url_photo_package;
    private String key;

    public ModelPackage(){

    }

    public ModelPackage(String package_name, String price, String detail, String url_photo_package) {
        this.package_name = package_name;
        this.price = price;
        this.detail = detail;
        this.url_photo_package = url_photo_package;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getUrl_photo_package() {
        return url_photo_package;
    }

    public void setUrl_photo_package(String url_photo_package) {
        this.url_photo_package = url_photo_package;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
