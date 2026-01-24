package com.liquidpixel.main.components.sprite;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TileableComponent implements Component {

    @JsonProperty
    String region;

    @JsonProperty
    private GridPoint2 lastKnownPosition;

    @JsonCreator
    public TileableComponent(
        @JsonProperty("region") String region,
        @JsonProperty("lastKnownPosition") GridPoint2 lastKnownPosition
    ) {
        this.region = region;
        this.lastKnownPosition = lastKnownPosition;
    }

    public TileableComponent(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public GridPoint2 getLastKnownPosition() {
        return lastKnownPosition;
    }

    public void setLastKnownPosition(GridPoint2 lastKnownPosition) {
        this.lastKnownPosition = lastKnownPosition;
    }
}
