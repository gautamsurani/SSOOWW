package com.serviceonwheel.model;

import java.io.Serializable;

/**
 * Created by welcome on 14-10-2016.
 */
public class CityListModel implements Serializable {
    String CityId;
    String name;

    public CityListModel() {
    }

    public CityListModel(String cityId, String name) {
        CityId = cityId;
        this.name = name;
    }


    public String getCityId() {
        return CityId;
    }

    public void setCityId(String cityId) {
        this.CityId = cityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
