package com.liquidpixel.main.interfaces.managers;

import com.liquidpixel.main.interfaces.services.ITerrainService;
import com.liquidpixel.main.model.terrain.TilesetConfig;
import com.liquidpixel.main.utils.RandomCollection;

import java.util.Map;

public interface ITerrainManager {
    ITerrainService getTerrainService();

    TilesetConfig getTilesetConfigurations();

    Map<Integer, RandomCollection<Integer>> getVariantCollections();

    Map<Integer, RandomCollection<Integer>> getItemCollections();

    void reset();
}
