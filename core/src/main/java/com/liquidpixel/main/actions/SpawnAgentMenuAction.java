package com.liquidpixel.main.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.selection.api.IClickAction;
import com.liquidpixel.main.interfaces.services.IAgentService;
import com.liquidpixel.main.interfaces.services.ICameraService;
import com.liquidpixel.main.interfaces.services.ISelectionService;

public class SpawnAgentMenuAction implements IClickAction {

    ISelectionService selectionService;
    ICameraService cameraService;
    IAgentService agentService;
    String key;
    Entity ghost;

    public SpawnAgentMenuAction(ISelectionService selectionService, ICameraService cameraService, IAgentService agentService, String key, Entity ghost) {
        this.selectionService = selectionService;
        this.cameraService = cameraService;
        this.agentService = agentService;
        this.key = key;
        this.ghost = ghost;
    }

    @Override
    public IClickAction execute(GridPoint2 position) {

        Vector2 clickedPosition = cameraService.getUnprojectedCursorPosition();
        Vector2 newPosition = new Vector2((float) Math.floor(clickedPosition.x), (float) Math.floor(clickedPosition.y));

        Entity entity = agentService.spawnAgent(position, key);
        selectionService.setSelectionAllowed(false);

        return this;
    }

    @Override
    public boolean isOneShot() {
        return true;
    }

    @Override
    public IClickAction exit() {
        GameResources.get().getEngine().removeEntity(ghost);
        selectionService.setSelectionAllowed(true);
        return null;
    }
}
