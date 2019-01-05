package com.serviceonwheel.model;

public class Ongoing_Model {

    private String orderID, display_orderID, order_date, remark,
            current_status, service_name, service_desc, amount,
            order_month, payment_status, date_of_appointment;

    public String getOrderID() {
        return orderID;
    }

    public String getOrder_month() {
        return order_month;
    }

    public void setOrder_month(String order_month) {
        this.order_month = order_month;
    }

    public void setOrderID(String orderID) {

        this.orderID = orderID;
    }

    public String getDisplay_orderID() {
        return display_orderID;
    }

    public String getDate_of_appointment() {
        return date_of_appointment;
    }

    public void setDate_of_appointment(String date_of_appointment) {
        this.date_of_appointment = date_of_appointment;
    }

    public void setDisplay_orderID(String display_orderID) {
        this.display_orderID = display_orderID;

    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCurrent_status() {
        return current_status;
    }

    public void setCurrent_status(String current_status) {
        this.current_status = current_status;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getService_desc() {
        return service_desc;
    }

    public void setService_desc(String service_desc) {
        this.service_desc = service_desc;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }
}
