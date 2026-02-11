package com.liquidpixel.main.components.ai.actions;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.interfaces.IStatusComponent;
import com.liquidpixel.main.model.status.WorkState;

public class HarvestComponent implements Component, IStatusComponent {

    public WorkState state = WorkState.IDLE;

    String itemName;

    GridPoint2 focusPoint;

    GridPoint2 interactionPoint;

    public HarvestComponent(
        WorkState state,
        String itemName,
        GridPoint2 location,
        GridPoint2 interactionPoint
    ) {
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
