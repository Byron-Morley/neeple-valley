package com.liquidpixel.main.components;

import com.badlogic.ashley.core.Component;

public class PathComponent implements Component {
    float modifier;

    public PathComponent(float modifier) {
        this.modifier = modifier;
    }

    public float getModifier() {
        return modifier;
    }
}
