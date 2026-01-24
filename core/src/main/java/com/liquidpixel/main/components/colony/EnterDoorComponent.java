package com.liquidpixel.main.components.colony;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.utils.Mappers;

public class EnterDoorComponent implements Component {

    public enum State {IDLE, OPENING, WALKING, OPEN, CLOSE}

    public State state = State.IDLE;

    Entity door;

    GridPoint2 doorStep;
    GridPoint2 inside;

    public EnterDoorComponent(Entity door) {
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
            doorStep = Mappers.position.get(getDoor()).getGridPosition().add(new GridPoint2(0, -1));
        }
        return doorStep;
    }

    public GridPoint2 getInside() {
        if (inside == null) {
            inside = Mappers.position.get(getDoor()).getGridPosition();
        }
        return inside;
    }
}
