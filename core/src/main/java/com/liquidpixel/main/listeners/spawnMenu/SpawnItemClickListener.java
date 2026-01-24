package com.liquidpixel.main.listeners.spawnMenu;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.liquidpixel.main.actions.SpawnItemMenuAction;
import com.liquidpixel.main.interfaces.services.ICameraService;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.services.ISelectionService;

public class SpawnItemClickListener extends ClickListener {

    ISelectionService selectionService;
    ICameraService cameraService;
    IItemService itemService;
    String key;
    String previousKey;

    public SpawnItemClickListener(
        String key,
        ISelectionService selectionService,
        ICameraService cameraService,
        IItemService itemService
    ) {
        this.cameraService = cameraService;
        this.selectionService = selectionService;
        this.itemService = itemService;
        this.key = key;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        Gdx.app.log("SpawnItemClickListener", key);
        selectionService.clearLeftClickActions();

        if (previousKey == null || !previousKey.equals(key)) {
            previousKey = key;
        }

        Entity ghost = itemService.getGhostItem(key, 1).build();
        itemService.spawnAndPickupItem(ghost);
        selectionService.setSelectionAllowed(false);
        selectionService.addLeftClickAction(new SpawnItemMenuAction(selectionService, cameraService, itemService, key, ghost));
    }
}
