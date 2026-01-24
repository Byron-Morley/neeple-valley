package com.liquidpixel.main.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.components.AreaSpawnConfigurationComponent;
import com.liquidpixel.main.components.TileSelectionComponent;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.main.services.PlayerInputService;
import com.liquidpixel.main.utils.Mappers;

public class SpawnConfigurationSystem extends IteratingSystem {

    IPlayerInputService playerInputService;
    ICameraService cameraService;
    IItemService itemService;
    ISelectionService selectionService;
    ISettlementService settlementService;


    public SpawnConfigurationSystem(
        ICameraService cameraService,
        PlayerInputService playerInputService,
        IItemService itemService,
        ISelectionService selectionService,
        ISettlementService settlementService
        ) {
        super(Family.all(AreaSpawnConfigurationComponent.class).get());
        this.cameraService = cameraService;
        this.playerInputService = playerInputService;
        this.itemService = itemService;
        this.selectionService = selectionService;
        this.settlementService = settlementService;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AreaSpawnConfigurationComponent area = entity.getComponent(AreaSpawnConfigurationComponent.class);
        GridPoint2 origin = Mappers.position.get(entity).getGridPosition();
        GridPoint2 cursorPosition = cameraService.getCursorGridLocation();

        int width, height;

        if (cursorPosition.x >= origin.x) {
            width = cursorPosition.x - origin.x + 1;
        } else {
            width = cursorPosition.x - origin.x - 1;
        }

        if (cursorPosition.y >= origin.y) {
            height = cursorPosition.y - origin.y + 1;
        } else {
            height = cursorPosition.y - origin.y - 1;
        }

        if (Mappers.tileSelection.has(entity)) {
            Mappers.tileSelection.get(entity).setSize(width, height);
        } else {
            entity.add(new TileSelectionComponent(width, height));
        }

//        if (playerInputService.isRightClicking() && !area.isCancelled()) {
//            area.setCancelled(true);
//
//            System.out.println("Spawn configuration system");
//            String itemName = Mappers.item.get(entity).getName();
//            Entity ghost = itemService.getGhostBuilding(itemName).build();
//            selectionService.setSpawnEntity(ghost);
//            itemService.spawnAndPickupItem(ghost);
//            selectionService.setSelectionAllowed(false);
//
//            selectionService.setAlternateAction(() -> {
//                if (selectionService.isValidBuildingSpot()) {
//                    Vector2 clickedPosition = cameraService.getUnprojectedCursorPosition();
//                    GridPoint2 newPosition = new GridPoint2((int) Math.floor(clickedPosition.x), (int) Math.floor(clickedPosition.y));
//                    selectionService.setSelectionAllowed(true);
//                    settlementService.buildFoundationInSettlement(itemName, newPosition);
//                }
//            });
//
//            getEngine().removeEntity(entity);
//        }
    }
}
