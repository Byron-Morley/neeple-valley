package com.liquidpixel.main.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.model.building.CaptionPattern;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class HarvestProviderComponent implements Component {

    @JsonProperty
    String pattern;

    @JsonIgnore
    List<GridPoint2> points;

    @JsonProperty
    String resource;

    @JsonProperty
    float timeLeft = 0;

    @JsonProperty
    int unitSize = 0;

    @JsonProperty
    boolean active = false;

    @JsonCreator
    public HarvestProviderComponent(
        @JsonProperty("pattern") String pattern,
        @JsonProperty("resource") String resource
    ) {
        this.pattern = pattern;
        this.resource = resource;
        CaptionPattern captionPattern = CaptionPattern.valueOf(pattern);
        points = captionPattern.getGridPoints();
    }

    @JsonProperty
    public String getPattern() {
        return pattern;
    }

    @JsonIgnore
    public List<GridPoint2> getPoints() {
        return points;
    }

    @JsonProperty
    public String getResource() {
        return resource;
    }

    public float getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(float timeLeft) {
        this.timeLeft = timeLeft;
    }

    public int getUnitSize() {
        return unitSize;
    }

    public void setUnitSize(int unitSize) {
        this.unitSize = unitSize;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
