package com.liquidpixel.main.generators.map;

import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.interfaces.MapConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MapConfig implements MapConfiguration {
    final int worldWidth;
    final int worldHeight;
    final int chunkStartLocationX;
    final int chunkStartLocationY;
    final int cameraStartLocationX;
    final int cameraStartLocationY;
    final int chunkWidth;
    final int chunkHeight;
    final int chunkCountX;
    final int chunkCountY;
    List<GridPoint2> homeArea;

    public MapConfig(int worldWidth, int worldHeight, int chunkWidth, int chunkHeight) {
        this(worldWidth, worldHeight, chunkWidth, chunkHeight, 2, 2);
    }

    public MapConfig(int worldWidth, int worldHeight, int chunkWidth, int chunkHeight, int homeWidth, int homeHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.chunkWidth = chunkWidth;
        this.chunkHeight = chunkHeight;
        this.chunkStartLocationX = worldWidth / 2 / chunkWidth;
        this.chunkStartLocationY = worldHeight / 2 / chunkHeight;
        this.chunkCountX = worldWidth / chunkWidth;
        this.chunkCountY = worldHeight / chunkHeight;
        this.cameraStartLocationX = worldWidth / 2;
        this.cameraStartLocationY = worldHeight / 2;
        this.homeArea = generateHomeArea(homeWidth, homeHeight, chunkCountX, chunkCountY);
    }

    public MapConfig(int worldWidth, int worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.chunkWidth = 32;
        this.chunkHeight = 32;
        this.chunkStartLocationX = worldWidth / 2 / chunkWidth;
        this.chunkStartLocationY = worldHeight / 2 / chunkHeight;
        this.chunkCountX = worldWidth / chunkWidth;
        this.chunkCountY = worldHeight / chunkHeight;
        this.cameraStartLocationX = worldWidth / 2;
        this.cameraStartLocationY = worldHeight / 2;
    }

    public List<GridPoint2> generateHomeArea(int homeWidth, int homeHeight, int worldWidth, int worldHeight) {

        int width = correctHomeLength(worldWidth, homeWidth);
        int height = correctHomeLength(worldHeight, homeHeight);

        homeArea = new ArrayList<>();
        int centerX = worldWidth / 2;
        int centerY = worldHeight / 2;
        int x = centerX - width / 2;
        int y = centerY - height / 2;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                homeArea.add(new GridPoint2(x + i, y + j));
            }
        }

        return homeArea;
    }

    public int correctHomeLength(int worldLength, int homeLength) {
        if (worldLength % 2 != 0) {
            return homeLength - 1;
        } else {
            return homeLength;
        }
    }


    public int getWorldWidth() {
        return worldWidth;
    }

    public int getWorldHeight() {
        return worldHeight;
    }

    public int getChunkStartLocationX() {
        return chunkStartLocationX;
    }

    public int getChunkStartLocationY() {
        return chunkStartLocationY;
    }

    public int getChunkWidth() {
        return chunkWidth;
    }

    public int getChunkHeight() {
        return chunkHeight;
    }


    public int getChunkCountX() {
        return chunkCountX;
    }

    public int getChunkCountY() {
        return chunkCountY;
    }

    public String toString() {
        return "worldWidth: " + worldWidth + "\n"
            + "worldHeight: " + worldHeight + "\n"
            + "startLocationX: " + chunkStartLocationX + "\n"
            + "startLocationY: " + chunkStartLocationY + "\n"
            + "chunkWidth: " + chunkWidth + "\n"
            + "chunkHeight: " + chunkHeight + "\n"
            + "chunkCountX: " + chunkCountX + "\n"
            + "chunkCountY: " + chunkCountY + "\n"
            + "cameraStartLocationX: " + cameraStartLocationX + "\n"
            + "cameraStartLocationY: " + cameraStartLocationY + "\n";
    }

    public int getCameraStartLocationX() {
        return cameraStartLocationX;
    }

    public int getCameraStartLocationY() {
        return cameraStartLocationY;
    }

    public int getChunkCount() {
        return chunkCountX * chunkCountY;
    }

    public List<GridPoint2> getHomeArea() {
        return homeArea;
    }
}
