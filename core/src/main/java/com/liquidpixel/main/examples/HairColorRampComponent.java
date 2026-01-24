package com.liquidpixel.main.examples;

import com.badlogic.ashley.core.Component;

/**
 * Color ramp component for hair entities
 */
public class HairColorRampComponent implements Component {
    public float rampIndex;
    public boolean enabled;
    public String rampName;

    public HairColorRampComponent(float rampIndex, boolean enabled) {
        this.rampIndex = rampIndex;
        this.enabled = enabled;
        this.rampName = null;
    }

    public HairColorRampComponent(float rampIndex, boolean enabled, String rampName) {
        this.rampIndex = rampIndex;
        this.enabled = enabled;
        this.rampName = rampName;
    }
}
