package com.liquidpixel.main.services;


import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.interfaces.ITerrainItemManager;
import com.liquidpixel.main.interfaces.ITerrainItemService;


public class TerrainItemService implements ITerrainItemService {
    private final ITerrainItemManager terrainItemManager;

    public TerrainItemService(ITerrainItemManager terrainItemManager) {
        this.terrainItemManager = terrainItemManager;
    }

    @Override
    public void createVisibleTerrainItems(GridPoint2 cameraChunkPosition) {
        terrainItemManager.createVisibleTerrainItems(cameraChunkPosition);
    }

    @Override
    public void cullTerrainItems(GridPoint2 cameraChunkPosition) {
        terrainItemManager.cullTerrainItems(cameraChunkPosition);
    }

    @Override
    public GridPoint2 worldToChunkPosition(GridPoint2 worldPosition) {
        return terrainItemManager.worldToChunkPosition(worldPosition);
    }

    @Override
    public void createAllTerrainItems() {
        terrainItemManager.createAllTerrainItems();
    }

    @Override
    public void clearAllTerrainItems() {
        terrainItemManager.clearAllTerrainItems();
    }
}
