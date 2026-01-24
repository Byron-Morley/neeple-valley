package com.liquidpixel.main.utils.shape;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.utils.collisions.Collisions;
import com.liquidpixel.main.utils.collisions.interfaces.LineCollider;

public class Line extends Shape implements LineCollider {
    public float x2;
    public float y2;

    public Line(float x, float y, float x2, float y2) {
        super();
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void setPosition(Vector2 position) {
        setX(position.x);
        setY(position.y);
    }

    @Override
    public boolean contains(float x, float y) {
        return false;
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        this.render(shapeRenderer, x, y);
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, Vector2 position) {
        this.render(shapeRenderer, position.x, position.y);
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, float x, float y) {
        shapeRenderer.setColor(this.getColor());
        shapeRenderer.line(x, y, x2, y2);
    }

    public float getX2() {
        return x2;
    }

    public void setX2(float x2) {
        this.x2 = x2;
    }

    public float getY2() {
        return y2;
    }

    public void setY2(float y2) {
        this.y2 = y2;
    }

    @Override
    public float getPreviousX() {
        return 0;
    }

    @Override
    public float getPreviousY() {
        return 0;
    }

    public boolean isSlope() {
        float gradient = Collisions.getGradient(this);
        return (gradient != 0);
    }

    public float getGradient(){
        return Collisions.getGradient(this);
    }

}
