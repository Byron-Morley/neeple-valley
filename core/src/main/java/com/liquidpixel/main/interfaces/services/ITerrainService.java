package com.liquidpixel.main.interfaces.services;

import com.liquidpixel.main.model.terrain.TerrainDefinition;
import com.liquidpixel.main.model.terrain.TerrainItem;

public interface ITerrainService {
    TerrainDefinition getTerrainType(double value);

    TerrainDefinition getTerrainTypeByName(String terrainType);

    int getVariant(TerrainDefinition terrain);
    TerrainItem getItem(TerrainDefinition terrain);
}
