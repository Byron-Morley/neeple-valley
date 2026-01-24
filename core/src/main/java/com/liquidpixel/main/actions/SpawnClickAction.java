package com.liquidpixel.main.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.selection.api.IClickAction;
import com.liquidpixel.main.interfaces.services.ICameraService;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.interfaces.services.ISettlementService;
import com.liquidpixel.main.model.ui.MenuTreeItem;

public class SpawnClickAction implements IClickAction {

    String itemName;
    MenuTreeItem menuTreeItem;
    ISelectionService selectionService;
    ICameraService cameraService;
    ISettlementService settlementService;
    IItemService itemService;
    Entity ghost;

    public SpawnClickAction(String itemName, MenuTreeItem menuTreeItem, ISelectionService selectionService, ICameraService cameraService, ISettlementService settlementService, IItemService itemService, Entity ghost) {
        this.itemName = itemName;
        this.menuTreeItem = menuTreeItem;
        this.selectionService = selectionService;
        this.cameraService = cameraService;
        this.settlementService = settlementService;
        this.itemService = itemService;
        this.ghost = ghost;
    }

    @Override
    public IClickAction execute(GridPoint2 position) {
        if (selectionService.isValidBuildingSpot() && (settlementService.canIAfford(menuTreeItem.getName()))) {
            Vector2 clickedPosition = cameraService.getUnprojectedCursorPosition();
            GridPoint2 newPosition = new GridPoint2((int) Math.floor(clickedPosition.x), (int) Math.floor(clickedPosition.y));
            selectionService.setSelectionAllowed(true);
            settlementService.buildFoundationInSettlement(itemName, newPosition);
        }

        return this;
    }

    @Override
    public boolean isOneShot() {
        return false;
    }

    @Override
    public IClickAction exit() {
        System.out.println("SpawnClickAction exit");
        itemService.dispose(ghost);
        return null;
    }
}
