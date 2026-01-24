package com.liquidpixel.main.systems.inits;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.BodyComponent;
import com.liquidpixel.main.components.DoorComponent;
import com.liquidpixel.main.components.colony.BuildingComponent;
import com.liquidpixel.main.components.inits.BuildingDoorInitComponent;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.managers.EntityInteractionManager;
import com.liquidpixel.main.utils.Mappers;

public class BuildingDoorInitSystem extends IteratingSystem {
    IItemService itemService;

    public BuildingDoorInitSystem(IItemService itemService) {
        super(Family.all(BuildingDoorInitComponent.class, BuildingComponent.class).get());
        this.itemService = itemService;
    }


    @Override
    protected void processEntity(Entity building, float deltaTime) {

        BuildingComponent buildingComponent = Mappers.building.get(building);

        BodyComponent bodyComponent = Mappers.body.get(building);
        GridPoint2 interactionPoint = bodyComponent.getInteractionPoint();
        Entity door = itemService.getItem("houses/door").build();

//        EntityInteractionManager.getInstance().registerInteraction(building, door);

        DoorComponent doorComponent = Mappers.door.get(door);
        doorComponent.setBuilding(building);

        itemService.spawnItem(door, new GridPoint2(interactionPoint.x, interactionPoint.y));
        buildingComponent.setDoor(door);

        building.remove(BuildingDoorInitComponent.class);
    }
}
