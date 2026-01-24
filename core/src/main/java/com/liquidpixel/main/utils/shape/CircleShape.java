package com.liquidpixel.main.utils.shape;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class CircleShape extends Shape {
    public static final int CIRCLE_SEGMENTS = 30;
    private float radius;

    public CircleShape() {}

    public CircleShape(float radius, Vector2 position) {
        this.radius = radius;
        setPosition(position);
    }

    @Override
    public void setPosition(Vector2 position) {
        setX(position.x);
        setY(position.y);
    }

    @Override
    public boolean contains(float x2, float y2) {
        Vector2 center = new Vector2(x, y);
        Vector2 point = new Vector2(x2, y2);

        float distance = center.dst(point);

        return distance <= radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.circle(x, y, radius, CIRCLE_SEGMENTS);
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, Vector2 position) {
        shapeRenderer.circle(position.x, position.y, radius, CIRCLE_SEGMENTS);
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, float x, float y) {
        shapeRenderer.circle(x, y, radius, CIRCLE_SEGMENTS);
    }

    public float getRadius() {
        return radius;
    }
}
