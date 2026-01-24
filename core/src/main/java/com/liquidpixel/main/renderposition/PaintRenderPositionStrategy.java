package com.liquidpixel.main.renderposition;

import com.badlogic.gdx.math.Vector2;

public class PaintRenderPositionStrategy implements RenderPositionStrategy {
    @Override
    public Vector2 process(float x, float y) {
        return new Vector2(x - 0.5f, y - 0.5f);
    }
}
