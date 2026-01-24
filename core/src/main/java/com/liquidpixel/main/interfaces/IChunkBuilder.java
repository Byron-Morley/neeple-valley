package com.liquidpixel.main.interfaces;

import com.badlogic.gdx.math.GridPoint2;

public interface IChunkBuilder {

    void buildChunk(GridPoint2 location, int width, int height);
    void spawnTerrainItems(GridPoint2 location, int width, int height);
}
