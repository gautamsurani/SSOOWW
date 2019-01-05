package com.serviceonwheel.model;

import java.util.List;

public class LevelTwoService {
    String q_title, serviceID, name, service_price, display_service_price, notes, description;
    List<LevelThreeService> levelThreeServices;

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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<LevelThreeService> getLevelThreeServices() {
        return levelThreeServices;

    }

    public void setLevelThreeServices(List<LevelThreeService> levelThreeServices) {
        this.levelThreeServices = levelThreeServices;
    }

    public String getQ_title() {
        return q_title;
    }

    public void setQ_title(String q_title) {
        this.q_title = q_title;
    }

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
}
