package com.ark.bookedapps.Model;

public class ModelUser {

    private String email;
    private String username;
    private String no_telp;
    private String role;
    private String key;

    public ModelUser(){

    }

    public ModelUser(String email, String username, String no_telp, String role) {
        this.email = email;
        this.username = username;
        this.no_telp = no_telp;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNo_telp() {
        return no_telp;
    }

    public void setNo_telp(String no_telp) {
        this.no_telp = no_telp;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
