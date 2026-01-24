package com.liquidpixel.main.components.colony;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.core.core.Action;
import com.liquidpixel.main.utils.Mappers;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ExitDoorComponent implements Component {

    @JsonProperty
    public Action state = Action.CLOSED;

    Entity door;

    GridPoint2 doorStep;
    GridPoint2 inside;

    public ExitDoorComponent(Entity door) {
        this.door = door;
    }

    public Entity getDoor() {
        if (Mappers.building.has(door)) {
            BuildingComponent buildingComponent = Mappers.building.get(door);
            return buildingComponent.getDoor();
        }
        return door;
    }

    public GridPoint2 getDoorStep() {
        if (doorStep == null) {
            doorStep = Mappers.position.get(getDoor()).getGridPosition().add(new GridPoint2(1, -1));
        }
        return doorStep;
    }

    public GridPoint2 getInside() {
        if (inside == null) {
            inside = Mappers.position.get(getDoor()).getGridPosition().add(new GridPoint2(1, 0));
        }
        return inside;
    }
}
