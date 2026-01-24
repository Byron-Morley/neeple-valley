package com.liquidpixel.main.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.selection.api.IClickAction;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.services.ICameraService;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.listeners.build.ObstacleBuildListener;
import com.liquidpixel.main.model.terrain.TerrainItemState;
import com.liquidpixel.main.utils.Mappers;

import java.util.List;

public class DestroyClickAction implements IClickAction {

    ISelectionService selectionService;
    ICameraService cameraService;
    IItemService itemService;
    IWorldMap worldMap;
    ObstacleBuildListener obstacleBuildListener;
    Entity ghost;

    public DestroyClickAction(ISelectionService selectionService, ICameraService cameraService, IItemService itemService, IWorldMap worldMap, ObstacleBuildListener obstacleBuildListener, Entity ghost) {
        this.selectionService = selectionService;
        this.cameraService = cameraService;
        this.itemService = itemService;
        this.worldMap = worldMap;
        this.obstacleBuildListener = obstacleBuildListener;
        this.ghost = ghost;
    }

    @Override
    public IClickAction execute(GridPoint2 position) {

        Vector2 clickedPosition = cameraService.getUnprojectedCursorPosition();
        GridPoint2 newPosition = new GridPoint2((int) Math.floor(clickedPosition.x), (int) Math.floor(clickedPosition.y));
        List<Entity> entities = worldMap.getEntitiesAtPosition(newPosition);

        if (!entities.isEmpty()) {
            Entity entity = entities.get(0);
            if (entity != null) {
                obstacleBuildListener.removeBodyPositions(entities.get(0));

                //TODO add compensation for destroying a building
                itemService.dispose(entity);

                if (Mappers.terrainItem.has(entity)) {
                    worldMap.setTerrainItemState(Mappers.position.get(entity).getGridPosition(), TerrainItemState.REMOVED);
                }
            }
        }
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
