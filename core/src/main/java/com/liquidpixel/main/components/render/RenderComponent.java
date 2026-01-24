package com.liquidpixel.main.components.render;


import com.badlogic.gdx.graphics.Color;
import com.liquidpixel.main.renderposition.DefaultRenderPositionStrategyImpl;
import com.liquidpixel.main.renderposition.RenderPositionStrategy;
import com.liquidpixel.sprite.model.GameSprite;
import com.liquidpixel.main.model.RenderPriority;
import com.fasterxml.jackson.annotation.*;
import com.liquidpixel.sprite.api.component.IRenderComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



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

    @JsonProperty
    List<String> spriteNames;

    @JsonCreator
    public RenderComponent(
        @JsonProperty("spriteNames") List<String> spriteNames,
        @JsonProperty("priority") RenderPriority priority,
        @JsonProperty("width") int width,
        @JsonProperty("height") int height,
        @JsonProperty("color") Color color,
        @JsonProperty("renderPositionStrategyType") String renderPositionStrategyType) {

        this.sprites = new ArrayList<>();
        this.spriteNames = spriteNames;
        this.priority = priority;
        this.width = width;
        this.height = height;
        this.color = (color != null) ? color : new Color(Color.WHITE);
        this.renderPositionStrategy = createStrategy(renderPositionStrategyType);
    }

    private RenderPositionStrategy createStrategy(String strategyType) {
        try {
            return (RenderPositionStrategy) Class.forName(strategyType).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return new DefaultRenderPositionStrategyImpl();
        }
    }

    public RenderComponent(RenderPositionStrategy renderPositionStrategy, RenderPriority priority, Color color) {
        this.sprites = new ArrayList<>();
        this.renderPositionStrategy = renderPositionStrategy;
        this.priority = priority;
        this.color = color;
    }

    public RenderComponent(RenderPositionStrategy renderPositionStrategy, RenderPriority priority) {
        this.sprites = new ArrayList<>();
        this.renderPositionStrategy = renderPositionStrategy;
        this.priority = priority;
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

    @JsonProperty
    public List<String> getSpriteNames() {
        return sprites.stream()
            .map(sprite -> ((GameSprite) sprite).getSpriteName())
            .collect(Collectors.toList());
    }

    @JsonProperty("color")
    public Color getSerializedColor() {
        if (color.equals(new Color(1, 1, 1, 1))) {
            return null;
        }
        return color;
    }

    @JsonProperty
    private String getRenderPositionStrategyType() {
        return renderPositionStrategy.getClass().getName();
    }

    public void setRenderPositionStrategy(RenderPositionStrategy renderPositionStrategy) {
        this.renderPositionStrategy = renderPositionStrategy;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void setSprite(GameSprite sprite) {
        spriteNames.clear();
        this.sprites.add(sprite);
    }


}
