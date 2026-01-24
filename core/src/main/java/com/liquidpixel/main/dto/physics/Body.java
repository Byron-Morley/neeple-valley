package com.liquidpixel.main.dto.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.liquidpixel.main.utils.shape.Shape;
import com.liquidpixel.main.utils.collisions.interfaces.Collider;

public class Body implements Collider {

    public Vector2 position;
    public float width;
    public float height;
    private BodyType bodyType;
    public Vector2 offset = new Vector2(Vector2.Zero);
    public Vector2 previous;
    private Array<Fixture> fixtures = new Array<>();

    public Body(float x, float y, float width, float height) {
        previous = new Vector2(x, y);
        position = new Vector2(x, y);
        this.width = width;
        this.height = height;
        this.bodyType = BodyType.STATIC;
    }

    public Body(float width, float height) {
        previous = new Vector2(-200, -200);
        position = new Vector2(-200, -200);
        this.width = width;
        this.height = height;
        this.bodyType = BodyType.STATIC;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Fixture createFixture(String id, Shape shape) {
        Fixture fixture = new Fixture(this, id, shape);
        fixtures.add(fixture);
        return fixture;
    }

    public Array<Fixture> getFixtures() {
        return fixtures;
    }

    public void setFixtures(Array<Fixture> fixtures) {
        this.fixtures = fixtures;
    }

    public Fixture getFixture(String id) {
        for (Fixture fixture : fixtures) {
            if (fixture.id.equals(id)) {
                return fixture;
            }
        }
        return null;
    }

    public Vector2 getOffset() {
        return offset;
    }

    public void setOffset(Vector2 offset) {
        this.offset = offset;
    }

    public float calculateXPosition() {
        return this.position.x + this.width / 2 + offset.x;
    }

    public float calculateYPosition() {
        return this.position.y + this.height / 2 + offset.y;
    }

    public Vector2 getSpeed() {
        return new Vector2(position).sub(new Vector2(previous));
    }

    @Override
    public float getX() {
        return position.x;
    }

    @Override
    public float getY() {
        return position.y;
    }

    @Override
    public float getPreviousX() {
        return previous.x;
    }

    @Override
    public float getPreviousY() {
        return previous.y;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }
}
