package com.liquidpixel.main.interfaces;

import com.badlogic.gdx.math.GridPoint2;

import java.util.List;

public interface MapConfiguration {

    int getWorldWidth();

    int getWorldHeight();

    int getChunkStartLocationX();

    int getChunkStartLocationY();

    int getChunkWidth();

    int getChunkHeight();

    int getChunkCountX();

    int getChunkCountY();

    int getCameraStartLocationX();

    int getCameraStartLocationY();

    int getChunkCount();

    String toString();

    List<GridPoint2> getHomeArea();
}
