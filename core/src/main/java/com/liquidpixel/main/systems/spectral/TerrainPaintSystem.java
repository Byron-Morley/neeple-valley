package com.liquidpixel.main.systems.spectral;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.components.TerrainPaintingComponent;
import com.liquidpixel.main.engine.GameState;
import com.liquidpixel.main.interfaces.services.ICameraService;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.utils.Mappers;

public class TerrainPaintSystem extends EntitySystem {

    ImmutableArray<Entity> paintEntities;
    ICameraService cameraService;
    IWorldMap worldMap;
    IItemService itemService;

    public TerrainPaintSystem(ICameraService cameraService, IWorldMap worldMap, IItemService itemService) {
        this.cameraService = cameraService;
        this.worldMap = worldMap;
        this.itemService = itemService;
    }

    @Override
    public void addedToEngine(Engine engine) {
        paintEntities = engine.getEntitiesFor(Family.all(TerrainPaintingComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            for (Entity entity : paintEntities) {
                TerrainPaintingComponent paint = Mappers.painter.get(entity);
                Vector2 pos = cameraService.getUnprojectedCursorPosition();
                GridPoint2 newPosition = new GridPoint2((int) pos.x, (int) pos.y);

                if (worldMap.isInBounds(newPosition)) {
                    worldMap.updateTerrain(newPosition, paint.getTerrainType(), GameState.getCurrentTerrainId());
                    itemService.spawnItem(itemService.getItem(paint.getItemName(), 1).build(), newPosition);
                }
            }
        }
    }
}
