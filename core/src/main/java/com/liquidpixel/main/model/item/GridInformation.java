package com.liquidpixel.main.model.item;

import com.badlogic.gdx.graphics.Color;

public class GridInformation {
    int width;
    int height;
    Color color = new Color(1, 1, 1, 0.5f);
    float cellSize = 0.8f;
    boolean showInvalidTiles = true;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Color getColor() {
        return color;
    }

    public float getCellSize() {
        return cellSize;
    }

    public boolean isShowInvalidTiles() {
        return showInvalidTiles;
    }
}
