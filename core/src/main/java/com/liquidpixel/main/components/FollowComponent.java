package com.liquidpixel.main.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FollowComponent implements Component {

    @JsonProperty
    private Entity followTarget;

    @JsonProperty
    private Vector2 offset;

    @JsonCreator
    public FollowComponent(
        @JsonProperty("followTarget") Entity followTarget,
        @JsonProperty("offset") Vector2 offset
    ) {
        this.offset = offset;
        this.followTarget = followTarget;
    }

    public Entity getFollowTarget() {
        return followTarget;
    }

    public void setFollowTarget(Entity followTarget) {
        this.followTarget = followTarget;
    }

    public Vector2 getOffset() {
        return offset;
    }

    public void setOffset(Vector2 offset) {
        this.offset = offset;
    }

}
