package com.liquidpixel.main.dto.physics;

import com.liquidpixel.main.utils.shape.RectangleShape;
import com.liquidpixel.main.utils.shape.Shape;

public class Fixture {

    public String id;
    private Shape shape;
    private Body body;

    public Fixture(Body body, String id, Shape shape) {
        this.id = id;
        this.shape = shape;
        this.body = body;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public float getX() {
        return body.position.x + shape.getX();
    }

    public float getY() {
        return body.position.y + shape.getY();
    }

    public float getPreviousX() {
        return body.getPreviousX() + shape.getX();
    }

    public float getPreviousY() {
        return body.getPreviousY() + shape.getY();
    }


    public float getWidth() {
        RectangleShape rectangleShape = (RectangleShape) shape;
        return rectangleShape.getWidth();
    }

    public float getHeight() {
        RectangleShape rectangleShape = (RectangleShape) shape;
        return rectangleShape.getHeight();
    }


//    @Override
//    public float getBottom() {
//        return this.getY();
//    }
//
//    @Override
//    public float getTop() {
//        return this.getY() + getHeight();
//    }
//
//    @Override
//    public float getLeft() {
//        return this.getX();
//    }
//
//    @Override
//    public float getRight() {
//        return this.getX() + getWidth();
//    }
//


}
