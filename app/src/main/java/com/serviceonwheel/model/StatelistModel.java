package com.serviceonwheel.model;

import java.io.Serializable;

/**
 * Created by welcome on 14-10-2016.
 */
public class StatelistModel implements Serializable {
    String StateId;
    String name;

    public StatelistModel(String stateId, String name) {
        StateId = stateId;
        this.name = name;
    }

    public String getStateId() {
        return StateId;
    }

    public void setStateId(String stateId) {
        this.StateId = stateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
