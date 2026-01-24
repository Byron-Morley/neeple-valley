package com.liquidpixel.main.listeners.spawnMenu;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.actions.DestroyClickAction;
import com.liquidpixel.main.interfaces.*;
import com.liquidpixel.main.interfaces.services.ICameraService;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.listeners.build.ObstacleBuildListener;
import com.liquidpixel.main.model.ui.MenuTreeItem;
import com.liquidpixel.selection.api.IMenuClickBehavior;

public class DestroyClickListener implements IMenuClickBehavior {

    ISelectionService selectionService;
    ICameraService cameraService;
    IItemService itemService;
    IWorldMap worldMap;
    ObstacleBuildListener obstacleBuildListener;

    public DestroyClickListener(
        ISelectionService selectionService,
        ICameraService cameraService,
        IItemService itemService,
        IWorldMap worldMap,
        ObstacleBuildListener obstacleBuildListener
    ) {
        this.cameraService = cameraService;
        this.selectionService = selectionService;
        this.itemService = itemService;
        this.worldMap = worldMap;
        this.obstacleBuildListener = obstacleBuildListener;
    }

    @Override
    public void onClick(MenuTreeItem menuTreeItem) {
        selectionService.clearLeftClickActions();

        //TODO update this later to use bomb sprite
        Entity ghost = itemService.getGhostBuilding("icons/cross").build();
        itemService.spawnAndPickupItem(ghost);
        selectionService.setSelectionAllowed(false);
        selectionService.addLeftClickAction(new DestroyClickAction(selectionService, cameraService, itemService, worldMap, obstacleBuildListener, ghost));
    }
}
