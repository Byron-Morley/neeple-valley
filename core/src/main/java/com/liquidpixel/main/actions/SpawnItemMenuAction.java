package com.liquidpixel.main.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.selection.api.IClickAction;
import com.liquidpixel.main.interfaces.services.ICameraService;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.services.ISelectionService;

public class SpawnItemMenuAction implements IClickAction {

    ISelectionService selectionService;
    ICameraService cameraService;
    IItemService itemService;
    String key;
    Entity ghost;

    public SpawnItemMenuAction(ISelectionService selectionService, ICameraService cameraService, IItemService itemService, String key, Entity ghost) {
        this.selectionService = selectionService;
        this.cameraService = cameraService;
        this.itemService = itemService;
        this.key = key;
        this.ghost = ghost;
    }

    @Override
    public IClickAction execute(GridPoint2 position) {

            Vector2 clickedPosition = cameraService.getUnprojectedCursorPosition();
            GridPoint2 newPosition = new GridPoint2((int) Math.floor(clickedPosition.x), (int) Math.floor(clickedPosition.y));

            selectionService.setSelectionAllowed(true);
            itemService.spawnItem(itemService.getItem(key, 1).build(), newPosition);

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
}
