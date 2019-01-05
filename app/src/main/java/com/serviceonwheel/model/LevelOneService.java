package com.serviceonwheel.model;

import java.util.List;

public class LevelOneService {
    String serviceID, name, q_title, description, notes, service_price, display_service_price;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getService_price() {
        return service_price;
    }

    public void setService_price(String service_price) {
        this.service_price = service_price;
    }

    public String getDisplay_service_price() {
        return display_service_price;
    }

    public void setDisplay_service_price(String display_service_price) {
        this.display_service_price = display_service_price;
    }

    List<LevelTwoService> levelTwoServices;

    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQ_title() {
        return q_title;
    }

    public void setQ_title(String q_title) {
        this.q_title = q_title;
    }

    public List<LevelTwoService> getLevelTwoServices() {
        return levelTwoServices;
    }

    public void setLevelTwoServices(List<LevelTwoService> levelTwoServices) {
        this.levelTwoServices = levelTwoServices;
    }
}
