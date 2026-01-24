package com.liquidpixel.main.listeners.spawnMenu;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.actions.HarvestClickAction;
import com.liquidpixel.main.interfaces.*;
import com.liquidpixel.main.interfaces.services.ICameraService;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.interfaces.services.ISettlementService;
import com.liquidpixel.main.model.ui.MenuTreeItem;
import com.liquidpixel.selection.api.IMenuClickBehavior;

public class HarvestClickListener implements IMenuClickBehavior {

    ISelectionService selectionService;
    ICameraService cameraService;
    IItemService itemService;
    IWorldMap worldMap;
    ISettlementService settlementService;

    public HarvestClickListener(
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
        selectionService.clearLeftClickActions();

        //TODO update this later to use cross sprite
        Entity ghost = itemService.getGhostBuilding("ui/cross").build();
        itemService.spawnAndPickupItem(ghost);
        selectionService.setSelectionAllowed(false);
        selectionService.addLeftClickAction(new HarvestClickAction(selectionService, cameraService, itemService, worldMap, settlementService, ghost));
    }
}

