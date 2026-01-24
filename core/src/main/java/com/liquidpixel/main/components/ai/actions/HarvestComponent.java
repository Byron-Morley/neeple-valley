package com.liquidpixel.main.components.ai.actions;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.interfaces.IStatusComponent;
import com.liquidpixel.main.model.status.WorkState;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HarvestComponent implements Component, IStatusComponent {

    @JsonProperty
    public WorkState state = WorkState.IDLE;

    @JsonProperty
    String itemName;

    @JsonProperty
    GridPoint2 focusPoint;

    @JsonProperty
    GridPoint2 interactionPoint;

    @JsonCreator
    public HarvestComponent(
        @JsonProperty("state") WorkState state,
        @JsonProperty("itemName") String itemName,
        @JsonProperty("location") GridPoint2 location,
        @JsonProperty("interactionPoint") GridPoint2 interactionPoint) {
        this.state = state;
        this.itemName = itemName;
        this.focusPoint = location;
        this.interactionPoint = interactionPoint;
    }

    public HarvestComponent(String itemName, GridPoint2 focusPoint, GridPoint2 interactionPoint) {
        this.itemName = itemName;
        this.focusPoint = focusPoint;
        this.interactionPoint = interactionPoint;
    }

    public String getItemName() {
        return itemName;
    }

    public GridPoint2 getFocusPoint() {
        return focusPoint;
    }

    public GridPoint2 getInteractionPoint() {
        return interactionPoint;
    }

    @Override
    public WorkState getState() {
        return state;
    }
}
