package com.kostylev.investbot.models;

public class PortfolioItem {
    private String figi, instrumentType, name, averagePositionPrice, quantityLots;

    public PortfolioItem(String figi, String instrumentType, String name, String averagePositionPrice, String quantityLots){
        this.figi = figi;
        this.instrumentType = instrumentType;
        this.name = name;
        this.averagePositionPrice = averagePositionPrice;
        this.quantityLots = quantityLots;
    }

    public String getFigi() {
        return figi;
    }

    public void setFigi(String figi) {
        this.figi = figi;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(String instrumentType) {
        this.instrumentType = instrumentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAveragePositionPrice() {
        return averagePositionPrice;
    }

    public void setAveragePositionPrice(String averagePositionPrice) {
        this.averagePositionPrice = averagePositionPrice;
    }

    public String getQuantityLots() {
        return quantityLots;
    }

    public void setQuantityLots(String quantityLots) {
        this.quantityLots = quantityLots;
    }
}
