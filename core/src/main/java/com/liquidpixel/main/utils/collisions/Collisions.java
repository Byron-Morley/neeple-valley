package com.liquidpixel.main.utils.collisions;

import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.utils.shape.Shape;
import com.liquidpixel.main.utils.collisions.interfaces.RectangleCollider;
import com.liquidpixel.main.utils.shape.Line;

public class Collisions {


    public static boolean futureCollision(RectangleCollider c1, RectangleCollider c2, float deltaTime) {

        Shape c1Shape = (Shape) c1;
        Shape c2Shape = (Shape) c1;

        return c1.getX() + c1.getWidth() + c1Shape.getVelocity().x * deltaTime > c2.getX() + c2Shape.getVelocity().x * deltaTime
                && c1.getX() + c1Shape.getVelocity().x * deltaTime < c2.getX() + c2.getWidth() + c2Shape.getVelocity().x * deltaTime
                && c1.getY() + c1.getHeight() + c1Shape.getVelocity().y * deltaTime > c2.getY() + c2Shape.getVelocity().y * deltaTime
                && c1.getY() + c1Shape.getVelocity().y * deltaTime < c2.getY() + c2.getHeight() + c2Shape.getVelocity().y * deltaTime;
    }
    public static boolean collision(RectangleCollider c1, RectangleCollider c2) {
        return c1.getX() + c1.getWidth() > c2.getX()
                && c1.getX() < c2.getX() + c2.getWidth()
                && c1.getY() + c1.getHeight() > c2.getY()
                && c1.getY() < c2.getY() + c2.getHeight();
    }

    public static boolean bottomCollision(RectangleCollider c1, RectangleCollider c2) {
        return c1.getY() <= c2.getY() + c2.getHeight() &&
                c1.getPreviousY() >= c2.getPreviousY() + c2.getHeight();
    }

    public static boolean topCollision(RectangleCollider c1, RectangleCollider c2) {
        return c1.getTop() >= c2.getBottom() &&
                c1.getPreviousY() + c1.getHeight() <= c2.getPreviousY();
    }

    public static boolean leftCollision(RectangleCollider c1, RectangleCollider c2) {
        return c1.getLeft() <= c2.getRight() &&
                c1.getPreviousX() >= c2.getPreviousX() + c2.getWidth();
    }

    public static boolean rightCollision(RectangleCollider c1, RectangleCollider c2) {
        return c1.getRight() >= c2.getLeft() &&
                c1.getPreviousX() + c1.getWidth() <= c2.getPreviousX();
    }

    public static float getGradient(Line line) {
        return Collisions.getGradient(line.getX(), line.getY(), line.getX2(), line.getY2());
    }

    public static float getGradient(float x1, float y1, float x2, float y2) {

        float gradient = (x2 - x1);

        if (x2 - x1 == 0) {
            gradient = 1;
        }

        return (y2 - y1) / gradient;
    }

    public static Vector2 intersectPoint(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {

        float m1 = getGradient(x1, y1, x2, y2);
        float m2 = getGradient(x3, y3, x4, y4);

        if (m1 == m2) {
            return new Vector2(Vector2.Zero);
        }

        float x = (m1 * x1 - m2 * x3 + y3 - y1) / (m1 - m2);
        float y = m1 * (x - x1) + y1;

        return new Vector2(x, y);
    }

    public static boolean lineRect(float lineX1, float lineY1, float lineX2, float lineY2, float rectX, float rectY, float rectWidth, float rectHeight) {

        boolean left = lineLine(lineX1, lineY1, lineX2, lineY2, rectX, rectY, rectX, rectY + rectHeight);
        boolean right = lineLine(lineX1, lineY1, lineX2, lineY2, rectX + rectWidth, rectY, rectX + rectWidth, rectY + rectHeight);
        boolean top = lineLine(lineX1, lineY1, lineX2, lineY2, rectX, rectY, rectX + rectWidth, rectY);
        boolean bottom = lineLine(lineX1, lineY1, lineX2, lineY2, rectX, rectY + rectHeight, rectX + rectWidth, rectY + rectHeight);

        if (left || right || top || bottom) {
            return true;
        }
        return false;
    }

    public static boolean lineLine(Line line1, Line line2) {
        return Collisions.lineLine(line1.getX(), line1.getY(), line1.getX2(), line1.getY2(), line2.getX(), line2.getY(), line2.getX2(), line2.getY2());
    }

    public static boolean lineLine(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {

        float uA = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1));
        float uB = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1));

        if (uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1) {
            return true;
        }
        return false;
    }
}
