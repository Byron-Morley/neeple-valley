package com.liquidpixel.main.interfaces;

import com.liquidpixel.main.model.terrain.TerrainDefinition;
import com.liquidpixel.main.model.terrain.TerrainItem;

public interface INoiseModule {
    TerrainDefinition getTerrain(int x, int y);
    int getVariant(TerrainDefinition terrain);
    TerrainItem getItem(TerrainDefinition terrain);
}
