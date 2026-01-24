package com.liquidpixel.main.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.model.item.GridInformation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TileSelectionComponent implements Component {

    @JsonProperty
    int width;

    @JsonProperty
    int height;

    @JsonProperty
    boolean useBodyOffset = false;

    @JsonProperty
    boolean outline = false;

    Color color = new Color(1, 1, 1, 0.5f);

    float cellSize = 0.8f;

    boolean showInvalidTiles = true;

    public TileSelectionComponent() {

    }

    @JsonCreator
    public TileSelectionComponent(int width, int height, boolean useBodyOffset, boolean outline) {
        this.width = width;
        this.height = height;
        this.useBodyOffset = useBodyOffset;
        this.outline = outline;
    }


    public TileSelectionComponent(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public TileSelectionComponent(boolean useBodyOffset) {
        this.useBodyOffset = useBodyOffset;
    }

    public TileSelectionComponent(boolean useBodyOffset, boolean outline) {
        this.useBodyOffset = useBodyOffset;
        this.outline = outline;
    }

    public TileSelectionComponent(GridInformation gridInformation) {
        this.width = gridInformation.getWidth();
        this.height = gridInformation.getHeight();
        this.color = gridInformation.getColor();
        this.cellSize = gridInformation.getCellSize();
        this.showInvalidTiles = gridInformation.isShowInvalidTiles();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isUseBodyOffset() {
        return useBodyOffset;
    }

    public boolean isOutline() {
        return outline;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Color getColor() {
        return color;
    }

    public float getCellSize() {
        return cellSize;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setCellSize(float cellSize) {
        this.cellSize = cellSize;
    }

    public boolean showInvalidTiles() {
        return showInvalidTiles;
    }

    public void setShowInvalidTiles(boolean showInvalidTiles) {
        this.showInvalidTiles = showInvalidTiles;
    }

    public int getSize() {
        return Math.abs(width) * Math.abs(height);
    }

    public List<GridPoint2> getAbsolutePoints(GridPoint2 origin) {
        List<GridPoint2> points = new ArrayList<>();

        // Determine the actual start and end points based on width and height
        // If width or height is negative, we need to adjust the starting point
        int startX = width < 0 ? origin.x + width + 1 : origin.x;
        int startY = height < 0 ? origin.y + height + 1 : origin.y;

        // Calculate the absolute values for iteration
        int absWidth = Math.abs(width);
        int absHeight = Math.abs(height);

        // Collect all points in the rectangle
        for (int x = startX; x < startX + absWidth; x++) {
            for (int y = startY; y < startY + absHeight; y++) {
                points.add(new GridPoint2(x, y));
            }
        }

        return points;
    }
}
