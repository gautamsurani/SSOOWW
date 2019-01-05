package com.serviceonwheel.model;

import java.io.Serializable;

/**
 * Created by welcome on 14-10-2016.
 */
public class SelectStateModel implements Serializable {
    String name;
    String StateId;

    public SelectStateModel(String cityId, String name, String stateId) {

        this.name = name;
        StateId = stateId;
    }

    public String getStateId() {
        return StateId;
    }

    public void setStateId(String stateId) {
        StateId = stateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
