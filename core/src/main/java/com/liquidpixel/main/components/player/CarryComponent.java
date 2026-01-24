package com.liquidpixel.main.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.utils.Mappers;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarryComponent implements Component {

    @JsonProperty
    private Entity item;

    @JsonProperty
    public float timeSincePickup;

    @JsonProperty
    public float timeSincePutDown;

    @JsonCreator
    public CarryComponent(
        @JsonProperty("item") Entity item,
        @JsonProperty("timeSincePickup") float timeSincePickup,
        @JsonProperty("timeSincePutDown") float timeSincePutDown
    ) {
        this.item = item;
        this.timeSincePickup = timeSincePickup;
        this.timeSincePutDown = timeSincePutDown;
    }

    public CarryComponent(Entity item) {
        this.item = item;
    }

    public CarryComponent() {
    }

    public void setTimeSincePutDown(float timeSincePutDown) {
        this.timeSincePutDown = timeSincePutDown;
    }

    public void setTimeSincePickup(float timeSincePickup) {
        this.timeSincePickup = timeSincePickup;
    }

    @JsonIgnore
    public Entity getItem() {
        return item;
    }

    public void setItem(Entity item) {
        this.item = item;
    }

    @JsonIgnore
    public boolean isOccupied() {
        return item != null;
    }

}
