package com.liquidpixel.main.managers;

import com.liquidpixel.main.engine.GameState;
import com.liquidpixel.main.factories.ModelFactory;
import com.liquidpixel.main.interfaces.managers.ITerrainManager;
import com.liquidpixel.main.interfaces.services.ITerrainService;
import com.liquidpixel.main.model.terrain.TerrainDefinition;
import com.liquidpixel.main.model.terrain.TerrainItem;
import com.liquidpixel.main.model.terrain.TerrainVariant;
import com.liquidpixel.main.model.terrain.TilesetConfig;
import com.liquidpixel.main.services.TerrainService;
import com.liquidpixel.main.utils.RandomCollection;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TerrainManager implements ITerrainManager {

    Map<Integer, RandomCollection<Integer>> variantCollections;
    Map<Integer, RandomCollection<Integer>> itemCollections;
    TilesetConfig tilesetConfigurations;
    ITerrainService terrainService;
    Random variantRandom;
    Random terrainRandom;

    public TerrainManager() {
        System.out.println("TerrainManager init seed: " + GameState.getSeed());
        variantRandom = new Random(GameState.getSeed());
        terrainRandom = new Random(GameState.getSeed());
        tilesetConfigurations = ModelFactory.getTerrainModel();
        terrainService = new TerrainService(this);
        initializeCollections();
    }

    private void initializeCollections() {

        variantCollections = new HashMap<>();
        itemCollections = new HashMap<>();

        for (TerrainDefinition terrain : tilesetConfigurations.getTerrainDefinitions()) {
            initializeTerrain(terrain);
        }
    }

    public void initializeTerrain(TerrainDefinition terrainDef) {
        setupVariants(terrainDef);
        setupTerrainItems(terrainDef);
    }

    private void setupVariants(TerrainDefinition terrainDef) {
        RandomCollection<Integer> variantCollection = new RandomCollection<>(variantRandom);
        for (TerrainVariant variant : terrainDef.getTerrainVariants()) {
            variantCollection.add(variant.getProbability(), variant.getIndex());
        }
        variantCollections.put(terrainDef.getId(), variantCollection);
    }


    private void setupTerrainItems(TerrainDefinition terrainDef) {
        RandomCollection<Integer> itemCollection = new RandomCollection<>(terrainRandom);

        for (int i = 0; i < terrainDef.getTerrainItems().size(); i++) {
            TerrainItem item = terrainDef.getTerrainItems().get(i);
            double prob = item.getProbability();
            if (prob > 0) {
                itemCollection.add(prob, i);
            }
        }

        double totalProb = terrainDef.getTerrainItems().stream()
            .mapToDouble(TerrainItem::getProbability)
            .sum();
        if (totalProb < 1.0) {
            itemCollection.add(1.0 - totalProb, -1);
        }

        itemCollections.put(terrainDef.getId(), itemCollection);
    }


    public ITerrainService getTerrainService() {
        return terrainService;
    }

    public TilesetConfig getTilesetConfigurations() {
        return tilesetConfigurations;
    }

    public Map<Integer, RandomCollection<Integer>> getVariantCollections() {
        return variantCollections;
    }

    public Map<Integer, RandomCollection<Integer>> getItemCollections() {
        return itemCollections;
    }

    @Override
    public void reset() {
        System.out.println("TerrainManager seed: " + GameState.getSeed());
        variantRandom = new Random(GameState.getSeed());
        terrainRandom = new Random(GameState.getSeed());
        initializeCollections();
    }
}
