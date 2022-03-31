package com.ark.bookedapps.Model;

public class ModelWallet {
    private String name_wallet;
    private String number_wallet;
    private String key;

    public ModelWallet() {
    }

    public ModelWallet(String name_wallet, String number_wallet) {
        this.name_wallet = name_wallet;
        this.number_wallet = number_wallet;
    }

    public String getName_wallet() {
        return name_wallet;
    }

    public void setName_wallet(String name_wallet) {
        this.name_wallet = name_wallet;
    }

    public String getNumber_wallet() {
        return number_wallet;
    }

    public void setNumber_wallet(String number_wallet) {
        this.number_wallet = number_wallet;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
