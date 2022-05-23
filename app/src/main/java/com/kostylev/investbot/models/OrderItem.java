package com.kostylev.investbot.models;

public class OrderItem {
    private String id, figi, direction, type, status, initialOrderPrice;

    public OrderItem(String id, String figi, String direction, String type, String status, String initialOrderPrice){
        this.id = id;
        this.figi = figi;
        this.direction = direction;
        this.type = type;
        this.status = status;
        this.initialOrderPrice = initialOrderPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFigi() {
        return figi;
    }

    public void setFigi(String figi) {
        this.figi = figi;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInitialOrderPrice() {
        return initialOrderPrice;
    }

    public void setInitialOrderPrice(String initialOrderPrice) {
        this.initialOrderPrice = initialOrderPrice;
    }

}
