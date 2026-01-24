package com.liquidpixel.main.utils;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.core.core.Direction;

public final class SteeringUtils {

    private SteeringUtils() {

    }

    public static float vectorToAngle(Vector2 vector) {
        return (float) Math.atan2(-vector.x, vector.y);
    }

    public static Vector2 angleToVector(Vector2 outVector, float angle) {
        outVector.x = -(float) Math.sin(angle);
        outVector.y = (float) Math.cos(angle);

        return outVector;
    }

    public static float vectorsToAngle(Vector2 v1, Vector2 v2) {

        float deltaX = v2.x - v1.x;
        float deltaY = v2.y - v1.y;

        float angleRad = (float) Math.atan2(deltaX, deltaY);

        float angleDeg = (float) (angleRad * 180 / Math.PI);

        return angleDeg;
    }

    public static Direction getDirection(GridPoint2 currentPosition, GridPoint2 target) {
        return getDirection(new Vector2(currentPosition.x, currentPosition.y), new Vector2(target.x, target.y));
    }

    public static Direction getDirection(Vector2 currentPosition, GridPoint2 target) {
        return getDirection(currentPosition, new Vector2(target.x, target.y));
    }

    public static Direction getDirection(Vector2 currentPosition, Vector2 target) {
        float angle = SteeringUtils.vectorsToAngle(new Vector2(currentPosition), new Vector2(target));

        if (angle >= -45 && angle <= 45) {
            return Direction.UP;
        } else if (angle > 45 && angle <= 135) {
            return Direction.RIGHT;
        } else if (angle > 135 && angle <= 180) {
            return Direction.DOWN;
        } else if (angle > -180 && angle <= -135) {
            return Direction.DOWN;
        } else if (angle > -135 && angle <= -45) {
            return Direction.LEFT;
        } else {
            return Direction.DOWN;
        }
    }

    public static float getGradient(Vector2 v1, Vector2 v2) {

        float x1 = v1.x;
        float y1 = v1.y;

        float x2 = v2.x;
        float y2 = v2.y;

        float gradient = (x2 - x1);

        if (x2 - x1 == 0) {
            gradient = 1;
        }

        return (y2 - y1) / gradient;
    }
}
