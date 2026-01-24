package com.liquidpixel.main.utils.shape;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.utils.collisions.interfaces.CurveCollider;

public class CurveShape extends Shape implements CurveCollider {
    private Bezier bezier;
    private float segmentCount = 100f;

    public CurveShape(float x, float y, float width, float height) {

        int points = 4;

        Vector2[] controlPoints = new Vector2[points];

        controlPoints[0] = new Vector2(x, y);
        controlPoints[1] = new Vector2(x + width * 0.25f, height+y);
        controlPoints[2] = new Vector2(x + width * 0.75f, height+y);
        controlPoints[3] = new Vector2(x + width, y);

        bezier = new Bezier<Vector2>(controlPoints);
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


    public Line calculateLine(int i) {
        float t = i / segmentCount;
        Vector2 start = new Vector2();
        Vector2 end = new Vector2();
        bezier.valueAt(start, t);
        bezier.valueAt(end, t - 1 / segmentCount);
        return new Line(start.x, start.y, end.x, end.y);
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
        for (int i = 1; i <= segmentCount; ++i) {
            Line line = this.calculateLine(i);
            shapeRenderer.line(line.getX(), line.getY(), line.getX2(), line.getY2());
        }
    }

    public Bezier getBezier() {
        return bezier;
    }

    public void setBezier(Bezier bezier) {
        this.bezier = bezier;
    }

    public float getSegmentCount() {
        return segmentCount;
    }

    public void setSegmentCount(float segmentCount) {
        this.segmentCount = segmentCount;
    }

    @Override
    public float getPreviousX() {
        return 0;
    }

    @Override
    public float getPreviousY() {
        return 0;
    }
}
