package com.kostylev.investbot.models;

public class ShareItem {
    private String figi, ticker, name, nominal, currency, sector;

    public ShareItem(String figi, String ticker, String name, String nominal, String currency, String sector){
        this.figi = figi;
        this.ticker = ticker;
        this.name = name;
        this.nominal = nominal;
        this.currency = currency;
        this.sector = sector;
    }

    public String getFigi() {
        return figi;
    }

    public void setFigi(String figi) {
        this.figi = figi;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }
}
