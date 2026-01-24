package com.liquidpixel.main.components;

import com.badlogic.ashley.core.Component;
import com.liquidpixel.main.renderposition.DefaultRenderPositionStrategyImpl;
import com.liquidpixel.main.renderposition.RenderPositionStrategy;
import com.liquidpixel.main.utils.shape.Shape;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShapeComponent implements Component {

    @JsonProperty
    private Shape shape;

    private float lineThickness = 1.0f;

    @JsonProperty
    private RenderPositionStrategy renderPositionStrategy;
    {
        this.renderPositionStrategy = new DefaultRenderPositionStrategyImpl();
    }

    public ShapeComponent(Shape shape, float lineThickness) {
        this.shape = shape;
        this.lineThickness = lineThickness;
    }

    @JsonCreator
    public ShapeComponent(Shape shape, RenderPositionStrategy renderPositionStrategy) {
        this.renderPositionStrategy = renderPositionStrategy;
        this.shape = shape;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public RenderPositionStrategy getRenderPositionStrategy() {
        return renderPositionStrategy;
    }

    public void setRenderPositionStrategy(RenderPositionStrategy renderPositionStrategy) {
        this.renderPositionStrategy = renderPositionStrategy;
    }

    public float getLineThickness() {
        return lineThickness;
    }

    public void setLineThickness(float lineThickness) {
        this.lineThickness = lineThickness;
    }
}
