package com.liquidpixel.main.utils.shape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Shape {

    @JsonProperty
    public float x;

    @JsonProperty
    public float y;

    @JsonProperty
    private Vector2 velocity = new Vector2(Vector2.Zero);

    @JsonProperty
    public boolean attached;

    @JsonProperty
    protected ShapeRenderer.ShapeType shapeType = ShapeRenderer.ShapeType.Line;

    @JsonProperty
    protected Color color = new Color(1f, 0f, 0f, 0.5f);

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public ShapeRenderer.ShapeType getShapeType() {
        return shapeType;
    }

    public void setShapeType(ShapeRenderer.ShapeType shapeType) {
        this.shapeType = shapeType;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setExactPosition(Vector2 position) {
        this.setX(position.x);
        this.setY(position.y);
    }


    public abstract void setPosition(Vector2 position);

    @JsonIgnore
    public abstract boolean contains(float x, float y);

    public abstract void render(ShapeRenderer shapeRenderer);

    public abstract void render(ShapeRenderer shapeRenderer, Vector2 position);

    public abstract void render(ShapeRenderer shapeRenderer, float x, float y);

    public boolean isAttached() {
        return attached;
    }

    @JsonIgnore
    public void setAttached(boolean attached) {
        if (attached) this.setColor(Color.YELLOW);
        this.attached = attached;
    }

    @JsonIgnore
    public Shape withVelocity(Vector2 velocity) {
        this.velocity = velocity;
        return this;
    }

    public Vector2 getVelocity() {
        return velocity;
    }
}
