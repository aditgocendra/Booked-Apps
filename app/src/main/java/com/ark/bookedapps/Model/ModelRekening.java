package com.ark.bookedapps.Model;

public class ModelRekening {
    private String rekBank;
    private String noRek;
    private String key;

    public ModelRekening(){

    }

    public ModelRekening(String rekBank, String noRek) {
        this.rekBank = rekBank;
        this.noRek = noRek;
    }

    public String getRekBank() {
        return rekBank;
    }

    public void setRekBank(String rekBank) {
        this.rekBank = rekBank;
    }

    public String getNoRek() {
        return noRek;
    }

    public void setNoRek(String noRek) {
        this.noRek = noRek;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
