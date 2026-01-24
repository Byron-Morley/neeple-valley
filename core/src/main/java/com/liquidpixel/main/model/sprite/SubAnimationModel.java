package com.liquidpixel.main.model.sprite;

import com.badlogic.gdx.graphics.g2d.Animation;

public class SubAnimationModel {
    private Animation.PlayMode playMode;
    private float frameDuration;
    private CellModel[] cells;

    public CellModel[] getCells() {
        return cells;
    }

    public void setCells(CellModel[] cells) {
        this.cells = cells;
    }

    public Animation.PlayMode getPlayMode() {
        return playMode;
    }

    public void setPlayMode(String playMode) {
        this.playMode = Animation.PlayMode.valueOf(playMode);
    }

    public float getFrameDuration() {
        return frameDuration;
    }

    public boolean hasFrameRate() {
        return frameDuration != 0;
    }

    public void setFrameDuration(float frameDuration) {
        this.frameDuration = frameDuration;
    }
}
