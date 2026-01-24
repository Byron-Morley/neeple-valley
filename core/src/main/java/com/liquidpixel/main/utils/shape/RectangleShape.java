package com.liquidpixel.main.utils.shape;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.utils.collisions.interfaces.RectangleCollider;

public class RectangleShape extends Shape implements RectangleCollider {
    private float width;
    private float height;

    public RectangleShape(float width, float height, Vector2 position) {
        this.width = width;
        this.height = height;
        setPosition(position);
    }

    public RectangleShape(float width, float height, float x, float y) {
        this.width = width;
        this.height = height;
        setX(x);
        setY(y);
    }

    public RectangleShape(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    @Override
    public float getBottom() {
        return this.getY();
    }

    @Override
    public float getTop() {
        return this.getY() + height;
    }

    @Override
    public float getLeft() {
        return this.getX();
    }

    @Override
    public float getRight() {
        return this.getX() + width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public void setPosition(Vector2 position) {
        setX(position.x - width / 2);
        setY(position.y - height / 2);
    }

    @Override
    public void setExactPosition(Vector2 position) {
        setX(position.x);
        setY(position.y);
    }

    @Override
    public boolean contains(float x, float y) {
        return getX() <= x && x <= getX() + width && getY() <= y && y <= getY() + height;
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(this.getColor());
        shapeRenderer.rect(x, y, width, height);
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, Vector2 position) {
        shapeRenderer.setColor(this.getColor());
        shapeRenderer.rect(position.x, position.y, width, height);
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, float x, float y) {
        shapeRenderer.setColor(this.getColor());
        shapeRenderer.rect(x, y, width, height);
    }

    @Override
    public float getPreviousX() {
        return this.getX();
    }

    @Override
    public float getPreviousY() {
        return this.getY();
    }
}
