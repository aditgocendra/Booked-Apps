package com.ark.bookedapps.Model;

public class ModelInformation {

    private String salon_name;
    private String salon_location;
    private String salon_about;
    private String ordered;
    private String owner_salon;
    private String email_owner_salon;
    private String url_owner_image;
    private String key;

    public ModelInformation(){

    }

    public ModelInformation(String salon_name, String salon_location, String salon_about, String ordered, String owner_salon, String email_owner_salon, String url_owner_image) {
        this.salon_name = salon_name;
        this.salon_location = salon_location;
        this.salon_about = salon_about;
        this.ordered = ordered;
        this.owner_salon = owner_salon;
        this.email_owner_salon = email_owner_salon;
        this.url_owner_image = url_owner_image;
    }

    public String getSalon_name() {
        return salon_name;
    }

    public void setSalon_name(String salon_name) {
        this.salon_name = salon_name;
    }

    public String getSalon_location() {
        return salon_location;
    }

    public void setSalon_location(String salon_location) {
        this.salon_location = salon_location;
    }

    public String getSalon_about() {
        return salon_about;
    }

    public void setSalon_about(String salon_about) {
        this.salon_about = salon_about;
    }

    public String getOrdered() {
        return ordered;
    }

    public void setOrdered(String ordered) {
        this.ordered = ordered;
    }

    public String getOwner_salon() {
        return owner_salon;
    }

    public void setOwner_salon(String owner_salon) {
        this.owner_salon = owner_salon;
    }

    public String getEmail_owner_salon() {
        return email_owner_salon;
    }

    public void setEmail_owner_salon(String email_owner_salon) {
        this.email_owner_salon = email_owner_salon;
    }

    public String getUrl_owner_image() {
        return url_owner_image;
    }

    public void setUrl_owner_image(String url_owner_image) {
        this.url_owner_image = url_owner_image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
