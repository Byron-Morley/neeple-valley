package com.liquidpixel.main.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.AreaSpawnConfigurationComponent;
import com.liquidpixel.main.components.StorageGroupComponent;
import com.liquidpixel.main.components.TileSelectionComponent;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.selection.api.IClickAction;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.services.ICameraService;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.interfaces.services.ISettlementService;
import com.liquidpixel.main.utils.Mappers;

import java.util.List;

public class ManageAreaSelection implements IClickAction {

    Entity entity;
    ISelectionService selectionService;
    ICameraService cameraService;
    ISettlementService settlementService;
    IItemService itemService;
    IWorldMap worldMap;


    public ManageAreaSelection(Entity entity, ISelectionService selectionService, ICameraService cameraService, ISettlementService settlementService, IItemService itemService, IWorldMap worldMap) {
        this.entity = entity;
        this.selectionService = selectionService;
        this.cameraService = cameraService;
        this.settlementService = settlementService;
        this.itemService = itemService;
        this.worldMap = worldMap;
    }

    @Override
    public IClickAction execute(GridPoint2 position) {
        selectionService.setSelectionAllowed(false);

        if (selectionService.isValidBuildingSpot()) {
            entity.remove(AreaSpawnConfigurationComponent.class);
            TileSelectionComponent tileSelectionComponent = Mappers.tileSelection.get(entity);
            tileSelectionComponent.setCellSize(1f);
            tileSelectionComponent.setColor(new Color(0.1f, 0.0f, 0.0f, 0.3f));
            tileSelectionComponent.setShowInvalidTiles(false);

            PositionComponent positionComponent = Mappers.position.get(entity);
            spawnStorage(tileSelectionComponent.getAbsolutePoints(positionComponent.getGridPosition()));

            return reset();
        } else {
            System.out.println("Invalid building spot");
            return this;
        }
    }

    private void spawnStorage(List<GridPoint2> absolutePoints) {
        StorageGroupComponent storageGroupComponent = Mappers.storageGroup.get(entity);
        StorageComponent groupStorage = Mappers.storage.get(entity);
        TileSelectionComponent tileSelectionComponent = Mappers.tileSelection.get(entity);
        tileSelectionComponent.setColor(new Color(0.1f, 0.0f, 0.0f, 0.3f));

        for (GridPoint2 point : absolutePoints) {


//            you cant place on top of other storage so this is redundant

//            List<Entity> entities = worldMap.getEntitiesAtPosition(point);
//            Entity item = entities
//                .stream()
//                .filter(entity -> Mappers.item.get(entity).getName().equals("storage/invisible_storage"))
//                .findFirst()
//                .orElse(null);
//
//            if (item == null) {
//                Entity storage = itemService.getItem("storage/invisible_storage").build();
//                itemService.spawnItem(storage, point);
//                storageGroupComponent.addStorageSpot(point, storage);
//            } else {
//                storageGroupComponent.addStorageSpot(point, item);
//            }

            Entity storage = itemService.getItem("storage/invisible_storage").build();
            StorageComponent storageComponent = Mappers.storage.get(storage);
            storageComponent.setPriority(groupStorage.getPriority());
            itemService.spawnItem(storage, point);
            storageGroupComponent.addStorageSpot(point, storage);
        }
    }

    private AreaClickAction reset() {
        ItemComponent itemComponent = Mappers.item.get(entity);
        Entity ghost = itemService.getGhostBuilding(itemComponent.getName()).build();
        itemService.spawnAndPickupItem(ghost);
        selectionService.setSelectionAllowed(false);

        return new AreaClickAction(itemComponent.getName(), selectionService, cameraService, settlementService, itemService, worldMap, ghost);
    }

    @Override
    public boolean isOneShot() {
        return false;
    }

    @Override
    public IClickAction exit() {
        System.out.println("ManageAreaSelection exit");
        AreaClickAction areaClickAction = reset();
        GameResources.get().getEngine().removeEntity(entity);

        return areaClickAction;
    }
}
