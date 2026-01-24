package com.liquidpixel.main.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.components.render.RenderComponent;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.factories.ModelFactory;
import com.liquidpixel.selection.api.IClickAction;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.services.ICameraService;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.interfaces.services.ISettlementService;
import com.liquidpixel.main.model.item.Item;

public class AreaClickAction implements IClickAction {

    String itemName;
    ISelectionService selectionService;
    ICameraService cameraService;
    ISettlementService settlementService;
    IItemService itemService;
    IWorldMap worldMap;
    Entity ghost;

    public AreaClickAction(String itemName, ISelectionService selectionService, ICameraService cameraService, ISettlementService settlementService, IItemService itemService, IWorldMap worldMap, Entity ghost) {
        this.itemName = itemName;
        this.selectionService = selectionService;
        this.cameraService = cameraService;
        this.settlementService = settlementService;
        this.itemService = itemService;
        this.worldMap = worldMap;
        this.ghost = ghost;
    }

    @Override
    public IClickAction execute(GridPoint2 position) {

        Item item = ModelFactory.getItemsModel().get(itemName);

        if (selectionService.isValidBuildingSpot() && (settlementService.canIAfford(item.getRecipeName()))) {
            Vector2 clickedPosition = cameraService.getUnprojectedCursorPosition();
            GridPoint2 newPosition = new GridPoint2((int) Math.floor(clickedPosition.x), (int) Math.floor(clickedPosition.y));
            Entity entity = settlementService.buildInSettlement(itemName, newPosition);
            entity.remove(RenderComponent.class);
            exit();
            return new ManageAreaSelection(entity, selectionService, cameraService, settlementService, itemService, worldMap);
        }
        return this;

    }

    @Override
    public boolean isOneShot() {
        return false;
    }

    @Override
    public IClickAction exit() {
        //cancel the entire spawn process
        System.out.println("AreaClickAction exit");
        GameResources.get().getEngine().removeEntity(ghost);
        selectionService.setSelectionAllowed(true);

        return null;
    }
}
