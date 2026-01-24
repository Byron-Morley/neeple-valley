package com.liquidpixel.main.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.components.AssetComponent;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.components.storage.ProviderComponent;
import com.liquidpixel.main.components.ui.MarkComponent;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.selection.api.IClickAction;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.services.ICameraService;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.interfaces.services.ISettlementService;
import com.liquidpixel.main.utils.Mappers;

import java.util.List;

public class HarvestClickAction implements IClickAction {

    ISelectionService selectionService;
    ICameraService cameraService;
    IItemService itemService;
    IWorldMap worldMap;
    ISettlementService settlementService;
Entity ghost;

    public HarvestClickAction(ISelectionService selectionService, ICameraService cameraService, IItemService itemService, IWorldMap worldMap, ISettlementService settlementService, Entity ghost) {
        this.selectionService = selectionService;
        this.cameraService = cameraService;
        this.itemService = itemService;
        this.worldMap = worldMap;
        this.settlementService = settlementService;
        this.ghost = ghost;
    }

    @Override
    public IClickAction execute(GridPoint2 position) {

        Vector2 clickedPosition = cameraService.getUnprojectedCursorPosition();
        GridPoint2 newPosition = new GridPoint2((int) Math.floor(clickedPosition.x), (int) Math.floor(clickedPosition.y));
        List<Entity> entities = worldMap.getEntitiesAtPosition(newPosition);

        for (Entity entity : entities) {
            handleEntityHarvesting(entity, newPosition);
        }

        return this;
    }

    @Override
    public boolean isOneShot() {
        return false;
    }

    @Override
    public IClickAction exit() {
        GameResources.get().getEngine().removeEntity(ghost);
        selectionService.setSelectionAllowed(true);
        return null;
    }

    private void handleEntityHarvesting(Entity entity, GridPoint2 position) {
        if (Mappers.harvest.has(entity)) {
            if (Mappers.mark.has(entity)) {
                unMarkResource(entity);
            } else {
                markForHarvesting(entity, position);
            }
        } else if (Mappers.storageTile.has(entity)) {
            System.out.println("Pickup Selection");
            if (Mappers.mark.has(entity)) {
                unMarkResource(entity);
            } else {
                selectionService.markForPickingUp(entity, position);
            }
        } else {
            System.out.println("Invalid Selection");
        }
    }

    private void unMarkResource(Entity entity) {
        GameResources.get().getEngine().removeEntity(Mappers.mark.get(entity).getMark());
        entity.remove(MarkComponent.class);
        entity.remove(AssetComponent.class);
        entity.remove(ProviderComponent.class);
        SettlementComponent settlement = Mappers.settlement.get(settlementService.getSelectedSettlement());
        settlement.removeAsset(entity);
    }

    private void markForHarvesting(Entity entity, GridPoint2 position) {
        Entity settlement = settlementService.getSelectedSettlement();
        SettlementComponent settlementComponent = Mappers.settlement.get(settlement);
        Entity cross = itemService.getItem("ui/cross").build();
        itemService.spawnItem(cross, position);
        MarkComponent mark = new MarkComponent(cross);
        entity.add(new AssetComponent(settlement));
        entity.add(mark);
        settlementComponent.addAsset(entity, settlement);
    }
}
