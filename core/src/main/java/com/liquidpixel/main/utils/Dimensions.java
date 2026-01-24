package com.liquidpixel.main.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;

public class Dimensions {

    public static Map<Integer, GridPoint2> dimensions;

    static {

        // X = column
        // Y = row

        dimensions = new HashMap<>();
        dimensions.put(1, new GridPoint2(1, 1));
        dimensions.put(2, new GridPoint2(2, 1));
        dimensions.put(3, new GridPoint2(3, 1));
        dimensions.put(4, new GridPoint2(2, 2));
        dimensions.put(5, new GridPoint2(5, 1));
        dimensions.put(6, new GridPoint2(3, 2));
        dimensions.put(8, new GridPoint2(4, 2));
        dimensions.put(12, new GridPoint2(4, 3));
    }


    /* One meter in pixels */
    public static final float PX_PER_METER = 16f;
    /* Meters to pixels */
    public static final float METERS_PER_PX = 1 / PX_PER_METER;

    public static final float toMeters(int px) {
        return (float) px / PX_PER_METER;
    }

    public static final float toMeters(float px) {
        return px / PX_PER_METER;
    }

    public static final int toPixels(float meters) {
        return (int) (meters * PX_PER_METER);
    }

    public static final Vector2 calculateGlobalPositionInPixelsToMetersRelativeToCenter(float x, float y) {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        float relativeX = x - width / 2f;
        float relativeY = y - height / 2f;

        return new Vector2(toMeters(relativeX), toMeters(relativeY));
    }


}
