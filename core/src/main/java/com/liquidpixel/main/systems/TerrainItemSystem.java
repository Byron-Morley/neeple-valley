package com.liquidpixel.main.systems;


import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.camera.CameraComponent;
import com.liquidpixel.main.interfaces.ITerrainItemService;
import com.liquidpixel.main.utils.Mappers;

public class TerrainItemSystem extends IteratingSystem {
    private final ITerrainItemService terrainItemService;

    public TerrainItemSystem(ITerrainItemService terrainItemService) {
        super(Family.all(CameraComponent.class, PositionComponent.class).get());
        this.terrainItemService = terrainItemService;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // This will only process camera entities
        PositionComponent position = Mappers.position.get(entity);

        // Convert world position to chunk position
        GridPoint2 cameraWorldPos = new GridPoint2((int)position.getX(), (int)position.getY());
        GridPoint2 cameraChunkPos = terrainItemService.worldToChunkPosition(cameraWorldPos);

        // Update terrain items based on camera position
        terrainItemService.createVisibleTerrainItems(cameraChunkPos);
        terrainItemService.cullTerrainItems(cameraChunkPos);
    }
}
