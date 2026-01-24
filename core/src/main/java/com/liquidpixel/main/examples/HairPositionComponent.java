package com.liquidpixel.main.examples;

import com.badlogic.ashley.core.Component;

/**
 * Simple position component for hair entities
 */
public class HairPositionComponent implements Component {
    public float x;
    public float y;

    public HairPositionComponent(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
