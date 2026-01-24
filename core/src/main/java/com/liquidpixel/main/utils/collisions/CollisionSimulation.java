package com.liquidpixel.main.utils.collisions;

import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.utils.collisions.interfaces.Collider;
import com.liquidpixel.main.utils.shape.CurveShape;
import com.liquidpixel.main.utils.shape.Line;
import com.liquidpixel.main.utils.shape.RectangleShape;

import static com.liquidpixel.main.utils.collisions.Collisions.collision;
import static com.liquidpixel.main.utils.collisions.Collisions.futureCollision;

public class CollisionSimulation {

    private Collider colliderOne;
    private Collider colliderTwo;
    private float RATIO = 1f;
    private float deltaTime;
    public boolean futureCollision = false;
    public boolean currentCollision = false;
    public Vector2 rightIntersection;
    public Vector2 leftIntersection;
    public Vector2 futureRightIntersection;
    public Vector2 futureLeftIntersection;
    private Line collisionLine;

    public CollisionSimulation(Collider colliderOne, Collider colliderTwo) {
        this.colliderOne = colliderOne;
        this.colliderTwo = colliderTwo;
    }

    public boolean collide(float deltaTime) {
        this.deltaTime = deltaTime;

        if (colliderOne instanceof Line) {
            if (colliderTwo instanceof RectangleShape) {
                return collideLineRect((Line) colliderOne, (RectangleShape) colliderTwo);
            }
        } else if (colliderOne instanceof RectangleShape) {
            if (colliderTwo instanceof Line) {
                return collideLineRect((Line) colliderTwo, (RectangleShape) colliderOne);
            } else if (colliderTwo instanceof CurveShape) {
                return collideCurveRect((CurveShape) colliderTwo, (RectangleShape) colliderOne);
            } else if (colliderTwo instanceof RectangleShape) {
                return collideRectRect((RectangleShape) colliderOne, (RectangleShape) colliderTwo);
            }
        } else if (colliderOne instanceof CurveShape) {
            if (colliderTwo instanceof RectangleShape) {
                return collideCurveRect((CurveShape) colliderOne, (RectangleShape) colliderTwo);
            }
        }

        return false;
    }


    private boolean collideRectRect(RectangleShape rectangleOne, RectangleShape rectangleTwo) {
        currentCollision = collision(rectangleOne, rectangleTwo);
        futureCollision = futureCollision(rectangleOne, rectangleTwo, deltaTime);

        return (futureCollision || currentCollision);
    }

    private boolean collideCurveRect(CurveShape curveShape, RectangleShape rectangleShape) {
        boolean curveCollision = false;
        for (int i = 0; i <= curveShape.getSegmentCount(); ++i) {
            Line line = curveShape.calculateLine(i);
            if (collideLineRect(line, rectangleShape)) {
                curveCollision = true;
                setCollisionLine(line);
                break;
            }
        }
        return curveCollision;
    }

    private boolean collideLineRect(Line line, RectangleShape rectangleShape) {

        if (isSlope(line)) {
            futureCollision = isFutureCollisionLineRect(line, rectangleShape, new Vector2(0, 10));
            currentCollision = isCurrentCollisionLineRect(line, rectangleShape);
        } else {
            futureCollision = isFutureCollisionLineRect(line, rectangleShape, new Vector2(0, 1));
            currentCollision = isCurrentCollisionLineRect(line, rectangleShape);
        }

        rightIntersection = rightIntersection(line, rectangleShape);
        leftIntersection = leftIntersection(line, rectangleShape);

        return (futureCollision || currentCollision);
    }


    private void setFutureIntersection(Line line, RectangleShape rectangleShape) {
        futureRightIntersection = rightIntersection(line, rectangleShape);
        futureLeftIntersection = leftIntersection(line, rectangleShape);
    }

    private void setIntersection(Line line, RectangleShape rectangleShape) {
        rightIntersection = rightIntersection(line, rectangleShape);
        leftIntersection = leftIntersection(line, rectangleShape);
    }

    public Vector2 rightIntersection(Line line, RectangleShape rectangleShape) {
        return Collisions.intersectPoint(
            line.getX(),
            line.getY(),
            line.getX2(),
            line.getY2(),
            rectangleShape.getX() + rectangleShape.getWidth(),
            rectangleShape.getY(),
            rectangleShape.getX() + rectangleShape.getWidth(),
            rectangleShape.getY() + rectangleShape.getHeight()
        );
    }

    public Vector2 leftIntersection(Line line, RectangleShape rectangleShape) {
        return Collisions.intersectPoint(
            line.getX(),
            line.getY(),
            line.getX2(),
            line.getY2(),
            rectangleShape.getX(),
            rectangleShape.getY(),
            rectangleShape.getX(),
            rectangleShape.getY() + rectangleShape.getHeight()
        );
    }

    public boolean isFutureCollisionLineRect(Line line, RectangleShape rectangleShape, Vector2 mod) {
        return Collisions.lineRect(
            line.getX() + RATIO * mod.x,
            line.getY() + RATIO * mod.y,
            line.getX2() + RATIO * mod.x,
            line.getY2() + RATIO * mod.y,
            rectangleShape.getX() + rectangleShape.getVelocity().x * deltaTime,
            rectangleShape.getY() + rectangleShape.getVelocity().y * deltaTime,
            rectangleShape.getWidth(),
            rectangleShape.getHeight()
        );
    }

    public boolean isCurrentCollisionLineRect(Line line, RectangleShape rectangleShape, Vector2 mod) {
        return Collisions.lineRect(line.getX(),
            line.getY(),
            line.getX2(),
            line.getY2(),
            rectangleShape.getX(),
            rectangleShape.getY(),
            rectangleShape.getWidth(),
            rectangleShape.getHeight()
        );
    }

    public boolean isFutureCollisionLineRect(Line line, RectangleShape rectangleShape) {
        return this.isFutureCollisionLineRect(line, rectangleShape, new Vector2(Vector2.Zero));
    }

    public boolean isCurrentCollisionLineRect(Line line, RectangleShape rectangleShape) {
        return this.isCurrentCollisionLineRect(line, rectangleShape, new Vector2(Vector2.Zero));
    }

    private boolean isSlope(Line line) {
        float gradient = Collisions.getGradient(line);
        return (gradient != 0);
    }

    public Vector2 getRightIntersection() {
        return rightIntersection;
    }

    public Vector2 getLeftIntersection() {
        return leftIntersection;
    }

    public Line getCollisionLine() {
        return collisionLine;
    }

    public void setCollisionLine(Line collisionLine) {
        this.collisionLine = collisionLine;
    }
}
