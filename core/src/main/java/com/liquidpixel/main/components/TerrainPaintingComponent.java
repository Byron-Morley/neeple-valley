package com.liquidpixel.main.components;

import com.badlogic.ashley.core.Component;

public class TerrainPaintingComponent implements Component {

    String terrainType;
    String itemName;

    public TerrainPaintingComponent(String terrainType, String itemName) {
        this.terrainType = terrainType;
        this.itemName = itemName;
    }

    public String getTerrainType() {
        return terrainType;
    }

    public String getItemName() {
        return itemName;
    }
}
