package com.liquidpixel.main.components.render;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;

public class ColorRampComponent implements Component {
    private Texture colorRampTexture;
    private float rampIndex = 0.0f; // Which row in the color ramp texture
    private boolean enabled = true;

    public ColorRampComponent(Texture colorRampTexture) {
        this.colorRampTexture = colorRampTexture;
    }

    public ColorRampComponent(Texture colorRampTexture, float rampIndex) {
        this.colorRampTexture = colorRampTexture;
        this.rampIndex = rampIndex;
    }

    // Getters and setters
    public Texture getColorRampTexture() { return colorRampTexture; }
    public void setColorRampTexture(Texture colorRampTexture) { this.colorRampTexture = colorRampTexture; }
    public float getRampIndex() { return rampIndex; }
    public void setRampIndex(float rampIndex) { this.rampIndex = rampIndex; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
