package com.liquidpixel.main.helpers;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.DoNotRenderComponent;
import com.liquidpixel.main.components.colony.BuildingComponent;
import com.liquidpixel.main.utils.Mappers;

public class BuildingHelper {

    public static void spawnInBuilding(Entity person, Entity building) {
        BuildingComponent buildingComponent = Mappers.building.get(building);
        buildingComponent.addOccupant(person);
        person.add(new PositionComponent(Mappers.position.get(building).getPosition()));
        person.add(new DoNotRenderComponent());
    }
}
