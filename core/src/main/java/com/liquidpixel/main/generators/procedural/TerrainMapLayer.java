package com.liquidpixel.main.generators.procedural;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.liquidpixel.main.model.terrain.TerrainDefinition;

public class TerrainMapLayer extends TiledMapTileLayer {
    private TerrainDefinition terrain;
    private double[][] tiles;
    private int[][] autoTiles;

    public TerrainMapLayer(TerrainDefinition terrain, int width, int height, int tileWidth, int tileHeight) {
        super(width, height, tileWidth, tileHeight);
        this.terrain = terrain;
        tiles = new double[width][height];
    }
    public int[][] getAutoTiles() {
        return autoTiles;
    }

    public int getAutoTile(int x, int y) {
        return autoTiles[x][y];
    }
    public TerrainDefinition getTerrain() {
        return terrain;
    }
    public void setTerrain(TerrainDefinition terrain) {
        this.terrain = terrain;
    }
    public void addTile(int x, int y, double value){
        tiles[x][y] = value;
    }

    public void convertAutoTiles(int x, int y, int width, int height){
//        System.out.println("Converting tiles to auto tiles");
        int[][] balloonedArray = AutoTileUtils.balloonArrayAndShift(tiles, 1, x, y, width, height);
        autoTiles = AutoTileUtils.shrinkArray(balloonedArray);
    }
}
