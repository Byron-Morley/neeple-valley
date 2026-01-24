package com.liquidpixel.main.listeners.spawnMenu;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.liquidpixel.main.actions.AreaClickAction;
import com.liquidpixel.main.interfaces.*;
import com.liquidpixel.main.interfaces.services.ICameraService;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.interfaces.services.ISettlementService;
import com.liquidpixel.main.model.ui.MenuTreeItem;
import com.liquidpixel.selection.api.IMenuClickBehavior;

public class AreaClickListener implements IMenuClickBehavior {

    ISelectionService selectionService;
    ICameraService cameraService;
    IItemService itemService;
    ISettlementService settlementService;
    IWorldMap worldMap;
    String previousKey;

    public AreaClickListener(
        ISettlementService settlementService,
        ISelectionService selectionService,
        ICameraService cameraService,
        IItemService itemService,
        IWorldMap worldMap
    ) {
        this.settlementService = settlementService;
        this.cameraService = cameraService;
        this.selectionService = selectionService;
        this.itemService = itemService;
        this.worldMap = worldMap;
    }

    @Override
    public void onClick(MenuTreeItem menuTreeItem) {
        if (settlementService.canIAfford(menuTreeItem.getName())) {
            selectionService.clearLeftClickActions();

            String itemName = menuTreeItem.getSpawn();
            Gdx.app.log("SpawnAreaClickListener", itemName);

            if (previousKey == null || !previousKey.equals(itemName)) {
                previousKey = itemName;
            }

            Entity ghost = itemService.getGhostBuilding(itemName).build();
            itemService.spawnAndPickupItem(ghost);

            selectionService.setSelectionAllowed(false);
            selectionService.addLeftClickAction(new AreaClickAction(itemName, selectionService, cameraService, settlementService, itemService, worldMap, ghost));
        }
    }
}

