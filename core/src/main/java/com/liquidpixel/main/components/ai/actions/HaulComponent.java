package com.liquidpixel.main.components.ai.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.ashley.core.Component;
import com.liquidpixel.main.interfaces.IStatusComponent;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.model.status.WorkState;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HaulComponent implements Component, IStatusComponent {

    @JsonProperty
    public WorkState state = WorkState.IDLE;

    @JsonProperty
    IStorageItem item;

    Entity origin;

    @JsonProperty
    GridPoint2 itemPickupLocation;

    @JsonProperty
    GridPoint2 itemDropLocation;

    Entity destination;

    @JsonProperty
    GridPoint2 inaccessiblePosition = null;

    @JsonCreator
    public HaulComponent(
        @JsonProperty("state") WorkState state,
        @JsonProperty("itemName") IStorageItem item,
        @JsonProperty("itemPickupLocation") GridPoint2 itemPickupLocation,
        @JsonProperty("itemDropLocation") GridPoint2 itemDropLocation
    ) {
        this.state = state;
        this.item = item;
        this.itemPickupLocation = itemPickupLocation;
        this.itemDropLocation = itemDropLocation;
    }

    public HaulComponent(
        IStorageItem item,
        GridPoint2 itemPickupLocation,
        GridPoint2 itemDropLocation,
        Entity destination,
        Entity origin
    ) {
        this.item = item;
        this.itemPickupLocation = itemPickupLocation;
        this.itemDropLocation = itemDropLocation;
        this.destination = destination;
        this.origin = origin;
    }

    public String getItemName() {
        return item.getName();
    }

    public GridPoint2 getItemPickupLocation() {
        return itemPickupLocation;
    }

    public GridPoint2 getItemDropLocation() {
        return itemDropLocation;
    }

    public GridPoint2 getInaccessiblePosition() {
        return inaccessiblePosition;
    }

    public void setInaccessiblePosition(GridPoint2 inaccessiblePosition) {
        this.inaccessiblePosition = inaccessiblePosition;
    }

    public String toString() {
        return "HaulComponent: " + getItemName() + "\n" + state + "\n" + itemPickupLocation + "\n" + itemDropLocation;
    }

    @Override
    public WorkState getState() {
        return state;
    }

    public IStorageItem getItem() {
        return item;
    }

    public Entity getDestination() {
        return destination;
    }

    public void setItemDropLocation(GridPoint2 itemDropLocation) {
        this.itemDropLocation = itemDropLocation;
    }

    public Entity getOrigin() {
        return origin;
    }
}
