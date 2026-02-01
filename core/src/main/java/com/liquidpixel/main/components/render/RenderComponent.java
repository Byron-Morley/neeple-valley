package com.liquidpixel.main.components.render;


import com.badlogic.gdx.graphics.Color;
import com.liquidpixel.main.renderposition.RenderPositionStrategy;
import com.liquidpixel.main.renderposition.SpriteRenderPositionStrategy;
import com.liquidpixel.sprite.model.GameSprite;
import com.liquidpixel.main.model.RenderPriority;
import com.fasterxml.jackson.annotation.*;
import com.liquidpixel.sprite.api.component.IRenderComponent;

import java.util.ArrayList;
import java.util.List;


public class RenderComponent implements IRenderComponent {

    @JsonProperty
    private RenderPriority priority;

    @JsonProperty
    private int width = 1;

    @JsonProperty
    private int height = 1;

    @JsonProperty
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private Color color = new Color(1, 1, 1, 1);

    @JsonIgnore
    private List<GameSprite> sprites;

    @JsonIgnore
    private RenderPositionStrategy renderPositionStrategy;

    public RenderComponent(GameSprite sprite, RenderPositionStrategy renderPositionStrategy, RenderPriority priority, Color color) {
        this.sprites = new ArrayList<>();
        this.renderPositionStrategy = renderPositionStrategy;
        this.priority = priority;
        this.color = color;
        setSprite(sprite);
    }

    public RenderComponent(RenderPositionStrategy renderPositionStrategy, RenderPriority priority) {
        this.sprites = new ArrayList<>();
        this.renderPositionStrategy = renderPositionStrategy;
        this.priority = priority;
    }

    public RenderComponent(GameSprite sprite, RenderPositionStrategy renderPositionStrategy, RenderPriority priority) {
        this.sprites = new ArrayList<>();
        this.renderPositionStrategy = renderPositionStrategy;
        this.priority = priority;
        setSprite(sprite);
    }

    public RenderComponent() {
        renderPositionStrategy = new SpriteRenderPositionStrategy();
        priority = RenderPriority.AGENT;
        this.sprites = new ArrayList<>();
    }

    public List<GameSprite> getSprites() {
        return sprites;
    }

    public RenderPriority getPriority() {
        return priority;
    }

    public void setPriority(RenderPriority priority) {
        this.priority = priority;
    }

    public RenderPositionStrategy getRenderPositionStrategy() {
        return renderPositionStrategy;
    }

    public void clear() {
        this.sprites.clear();
    }

    public void add(GameSprite sprite) {
        this.sprites.add(sprite);
    }

    @Override
    public boolean hasSprites() {
        return !sprites.isEmpty();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setRenderPositionStrategy(RenderPositionStrategy renderPositionStrategy) {
        this.renderPositionStrategy = renderPositionStrategy;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void setSprite(GameSprite sprite) {
        this.sprites.add(sprite);
    }


}
