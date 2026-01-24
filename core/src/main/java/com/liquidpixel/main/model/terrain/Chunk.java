package com.liquidpixel.main.model.terrain;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.generators.procedural.Tile;
import com.liquidpixel.main.interfaces.IChunk;

public class Chunk extends TiledMapTileLayer implements IChunk {

    GridPoint2 location;
    GridPoint2 pixelLocation;
    static protected final int NUM_VERTICES = 20;
    float vertices[] = new float[NUM_VERTICES];

    public Chunk(GridPoint2 location, int width, int height, int tileWidth, int tileHeight) {
        super(width, height, tileWidth, tileHeight);
        this.location = location;
        this.pixelLocation = new GridPoint2(location.x * width, location.y * height);
    }

    public GridPoint2 getLocation() {
        return location;
    }

    public float[] getVertices() {
        return vertices;
    }

    public GridPoint2 getPixelLocation() {
        return pixelLocation;
    }

    public Tile getTile(int x, int y) {
        return (Tile) getCell(x, y).getTile();
    }

    public Tile getTile(GridPoint2 globalPosition) {

        int localX = Math.abs(globalPosition.x % getWidth());
        int localY = Math.abs(globalPosition.y % getHeight());

        return (Tile) getCell(localX, localY).getTile();
    }
}
