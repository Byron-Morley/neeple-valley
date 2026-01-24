package com.liquidpixel.main.renderposition;

import com.badlogic.gdx.math.Vector2;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonSubTypes({
    @JsonSubTypes.Type(value = ItemRenderPositionStrategy.class),
    @JsonSubTypes.Type(value = SpriteRenderPositionStrategy.class)
})
public interface RenderPositionStrategy {
    @JsonProperty
    Vector2 process(float x, float y);
}
