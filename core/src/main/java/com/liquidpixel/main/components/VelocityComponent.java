package com.liquidpixel.main.components;

import com.badlogic.ashley.core.Component;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VelocityComponent implements Component {
    @JsonProperty
    private final static float DEFAULT_MAX = 2f;

    @JsonProperty
    private float max;

    @JsonProperty
    private float velocity;

    @JsonCreator
    public VelocityComponent(@JsonProperty("velocity") float velocity) {
        this.velocity = velocity;
        this.max = DEFAULT_MAX;
    }

    public float getMax() {
        return max;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }
}
