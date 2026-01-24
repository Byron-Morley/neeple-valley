package com.liquidpixel.main.interfaces;

import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.generators.procedural.Tile;

public interface IChunk {
    GridPoint2 getLocation();
    Tile getTile(int x, int y);
}
