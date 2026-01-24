package com.liquidpixel.main.services;

import com.liquidpixel.main.interfaces.managers.ITerrainManager;
import com.liquidpixel.main.interfaces.services.ITerrainService;
import com.liquidpixel.main.model.terrain.TerrainDefinition;
import com.liquidpixel.main.model.terrain.TerrainItem;
import com.liquidpixel.main.utils.RandomCollection;

public class TerrainService implements ITerrainService {

    ITerrainManager terrainManager;

    public TerrainService(ITerrainManager terrainManager) {
        this.terrainManager = terrainManager;
    }

    @Override
    public TerrainDefinition getTerrainType(double value) {
        return terrainManager.getTilesetConfigurations().getTerrainType(value);
    }

    @Override
    public TerrainDefinition getTerrainTypeByName(String terrainType) {
        return terrainManager.getTilesetConfigurations().getTerrainTypeByName(terrainType);
    }

    @Override
    public int getVariant(TerrainDefinition terrain) {
        return terrainManager.getVariantCollections().get(terrain.getId()).next();
    }

    public TerrainItem getItem(TerrainDefinition terrain) {
        RandomCollection<Integer> itemCollection = terrainManager.getItemCollections().get(terrain.getId());
        try {
            Integer index = itemCollection.next();
            return index >= 0 ? terrain.getTerrainItems().get(index) : null;
        } catch (Exception e) {
            return null;
        }
    }
}
