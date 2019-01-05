package com.serviceonwheel.model;

public class OrderList {
    String id, name, orderID, date, amount, status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrderList(String id, String name, String orderID, String date, String amount, String status) {

        this.id = id;
        this.name = name;
        this.orderID = orderID;
        this.date = date;
        this.amount = amount;
        this.status = status;
    }
}
