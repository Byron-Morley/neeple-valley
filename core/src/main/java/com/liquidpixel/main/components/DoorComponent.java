package com.liquidpixel.main.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.components.colony.BuildingComponent;
import com.liquidpixel.main.utils.Mappers;

import java.util.ArrayList;
import java.util.List;

public class DoorComponent implements Component {

    Entity building;
    List<Entity> occupants;

    public DoorComponent() {
        this.occupants = new ArrayList<>();
    }

    public void addOccupant(Entity occupant) {

        if (hasBuildingAttached()) {
            BuildingComponent buildingComponent = Mappers.building.get(building);
            buildingComponent.addOccupant(occupant);
        } else {
            occupants.add(occupant);
        }
    }

    public boolean hasBuildingAttached() {
        return building != null && Mappers.building.has(building);
    }

    public void removeOccupant(Entity occupant) {
        if (hasBuildingAttached()) {
            BuildingComponent buildingComponent = Mappers.building.get(building);
            buildingComponent.removeOccupant(occupant);
        } else {
            occupants.remove(occupant);
        }
    }

    public boolean isEntityInside(Entity entity) {
        if (hasBuildingAttached()) {
            BuildingComponent buildingComponent = Mappers.building.get(building);
            return buildingComponent.getOccupants().contains(entity);
        }
        return occupants.contains(entity);
    }

    public Entity getBuilding() {
        return building;
    }

    public void setBuilding(Entity building) {
        this.building = building;
    }
}
