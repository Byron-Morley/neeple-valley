package com.liquidpixel.main.components.colony;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.List;

public class BuildingComponent implements Component {

    Entity door;

    boolean spawnDirtPatch = true;

    boolean hasDirtPatch;

    List<Entity> occupants;

    public BuildingComponent(Entity door) {
        this.door = door;
        this.occupants = new ArrayList<>();
    }

    public BuildingComponent() {
        this.occupants = new ArrayList<>();
    }

    public Entity getDoor() {
        return door;
    }

    public void setDoor(Entity door) {
        this.door = door;
    }

    public List<Entity> getOccupants() {
        return occupants;
    }

    public void addOccupant(Entity occupant) {
        occupants.add(occupant);
    }

    public void removeOccupant(Entity occupant) {
        occupants.remove(occupant);
    }

    public boolean isHasDirtPatch() {
        return hasDirtPatch;
    }

    public void setHasDirtPatch(boolean hasDirtPatch) {
        this.hasDirtPatch = hasDirtPatch;
    }

    public boolean isSpawnDirtPatch() {
        return spawnDirtPatch;
    }
}
