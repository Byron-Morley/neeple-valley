package com.liquidpixel.main.examples;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Simple sprite component for hair entities
 */
public class HairSpriteComponent implements Component {
    public TextureRegion textureRegion;

    public HairSpriteComponent(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }
}
