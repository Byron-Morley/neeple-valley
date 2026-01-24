package com.liquidpixel.main.listeners.spawnMenu;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.liquidpixel.main.actions.SpawnClickAction;
import com.liquidpixel.main.interfaces.services.ICameraService;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.interfaces.services.ISettlementService;
import com.liquidpixel.main.model.ui.MenuTreeItem;
import com.liquidpixel.selection.api.IMenuClickBehavior;

public class SpawnClickListener implements IMenuClickBehavior {

    ISelectionService selectionService;
    ICameraService cameraService;
    IItemService itemService;
    ISettlementService settlementService;
    String previousKey;

    public SpawnClickListener(
        ISettlementService settlementService,
        ISelectionService selectionService,
        ICameraService cameraService,
        IItemService itemService
    ) {
        this.settlementService = settlementService;
        this.cameraService = cameraService;
        this.selectionService = selectionService;
        this.itemService = itemService;
    }

    @Override
    public void onClick(MenuTreeItem menuTreeItem) {
        if (settlementService.canIAfford(menuTreeItem.getName())) {
            selectionService.clearLeftClickActions();

            String itemName = menuTreeItem.getSpawn();
            Gdx.app.log("SpawnBuildingClickListener", itemName);

            if (previousKey == null || !previousKey.equals(itemName)) {
                previousKey = itemName;
            }

            Entity ghost = itemService.getGhostBuilding(itemName).build();
            itemService.spawnAndPickupItem(ghost);
            selectionService.setSelectionAllowed(false);
            selectionService.addLeftClickAction(new SpawnClickAction(itemName, menuTreeItem, selectionService, cameraService, settlementService, itemService, ghost));
        }
    }
}

