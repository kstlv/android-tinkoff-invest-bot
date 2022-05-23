package com.kostylev.investbot.models;

public class AccountSandbox {
    private String id, accessLevel, openedDate, totalAmountCurrencies, totalAmountShares;

    public AccountSandbox(String id, String accessLevel, String openedDate, String totalAmountCurrencies, String totalAmountShares){
        this.id = id;
        this.accessLevel = accessLevel;
        this.openedDate = openedDate;
        this.totalAmountCurrencies = totalAmountCurrencies;
        this.totalAmountShares = totalAmountShares;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getOpenedDate() {
        return openedDate;
    }

    public void setOpenedDate(String openedDate) {
        this.openedDate = openedDate;
    }

    public String getTotalAmountCurrencies() {
        return totalAmountCurrencies;
    }

    public void setTotalAmountCurrencies(String totalAmountCurrencies) {
        this.totalAmountCurrencies = totalAmountCurrencies;
    }

    public String getTotalAmountShares() {
        return totalAmountShares;
    }

    public void setTotalAmountShares(String totalAmountShares) {
        this.totalAmountShares = totalAmountShares;
    }
}
