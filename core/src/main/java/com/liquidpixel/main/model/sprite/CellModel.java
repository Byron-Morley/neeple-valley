package com.liquidpixel.main.model.sprite;

public class CellModel {
    private int cell;

    private boolean flipX;
    private boolean flipY;

    public CellModel() {
    }

    public CellModel(int cell, boolean flipX, boolean flipY) {
        this.cell = cell;
        this.flipX = flipX;
        this.flipY = flipY;
    }

    public int getCell() {
        return cell;
    }

    public void setCell(int cell) {
        this.cell = cell;
    }

    public boolean isFlipX() {
        return flipX;
    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }

    public boolean isFlipY() {
        return flipY;
    }

    public void setFlipY(boolean flipY) {
        this.flipY = flipY;
    }
}
