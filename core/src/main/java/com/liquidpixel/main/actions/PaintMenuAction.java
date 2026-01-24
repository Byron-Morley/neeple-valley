package com.liquidpixel.main.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.TerrainPaintingComponent;
import com.liquidpixel.main.components.load.DontSaveComponent;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.selection.api.IClickAction;
import com.liquidpixel.main.interfaces.services.ISelectionService;

public class PaintMenuAction implements IClickAction {

    ISelectionService selectionService;
    String key;
    String itemName;
    Entity ghost;

    public PaintMenuAction(ISelectionService selectionService, String key, String itemName, Entity ghost) {
        this.selectionService = selectionService;
        this.key = key;
        this.itemName = itemName;
        this.ghost = ghost;
    }

    @Override
    public IClickAction execute(GridPoint2 position) {
        ghost.add(new TerrainPaintingComponent(key, itemName));
        ghost.add(new DontSaveComponent());
        return null;
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
