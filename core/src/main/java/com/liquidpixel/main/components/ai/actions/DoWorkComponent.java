package com.liquidpixel.main.components.ai.actions;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DoWorkComponent implements Component {

    public enum State {
        IDLE,
        MOVING_TO_WORKSHOP,
        SELECT_RECIPE,
        WORKING,
        COMPLETED,
        FAILED
    }

    @JsonProperty
    public State state = State.IDLE;

    @JsonProperty
    Entity workshop;

    public DoWorkComponent(Entity workshop) {
        this.workshop = workshop;
    }

    @JsonCreator
    public DoWorkComponent(State state, Entity workshop) {
        this.state = state;
        this.workshop = workshop;
    }

    public Entity getWorkshop() {
        return workshop;
    }
}
