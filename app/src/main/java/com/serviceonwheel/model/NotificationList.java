package com.serviceonwheel.model;

import java.io.Serializable;

public class NotificationList implements Serializable {

    String offer_ID;
    String title;
    String image;
    String message;
    String added_on;
    String shre_msg;

    public String getOffer_ID() {
        return offer_ID;
    }

    public void setOffer_ID(String offer_ID) {
        this.offer_ID = offer_ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAdded_on() {
        return added_on;
    }

    public void setAdded_on(String added_on) {
        this.added_on = added_on;
    }

    public String getShre_msg() {
        return shre_msg;
    }

    public void setShre_msg(String shre_msg) {
        this.shre_msg = shre_msg;
    }
}
