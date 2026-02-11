package com.liquidpixel.main.utils;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class LoopUtils {

    @FunctionalInterface
    public interface CoordinateProcessor {
        void process(GridPoint2 coordinate);
    }

    @FunctionalInterface
    public interface IntProcessor {
        void process(int value);
    }

    public static void insideOut(int width, int height, CoordinateProcessor processor) {

        int radius = (int) Math.ceil(width);
        int startX = (int) Math.floor(width / 2);
        int startY = (int) Math.floor(height / 2);

        insideOut(width, height, radius, startX, startY, processor);
    }

    public static void insideOut(int boundsX, int boundsY, int radius, int startX, int startY, CoordinateProcessor processor) {

        ArrayList<GridPoint2> visited = new ArrayList<>();

        for (int r = 0; r <= radius; r++) {
            for (int x = startX - r; x <= startX + r; x++) {
                for (int y = startY - r; y <= startY + r; y++) {
                    if (x >= 0 && x < boundsX && y >= 0 && y < boundsY) {
                        if (!visited.contains(new GridPoint2(x, y))) {
                            visited.add(new GridPoint2(x, y));
                            processor.process(new GridPoint2(x, y));
                        }
                    }
                }
            }
        }
    }

    public static void insideOut(int width, int height, GridPoint2 center, CoordinateProcessor processor) {
        insideOut(width, height, (coordinate) -> {

            int dx = (center.x - (width / 2)) + coordinate.x;
            int dy = (center.y - (height / 2)) + coordinate.y;

            GridPoint2 position = new GridPoint2(dx, dy);
            processor.process(position);
        });
    }

    public static void loopArrayFromMiddle(int length, IntProcessor processor) {
        int middle = length / 2;

        processor.process(middle);

        for (int i = 1; i <= middle; i++) {
            if (middle - i >= 0) {
                processor.process(middle - i);
            }

            if (middle + i < length) {
                processor.process(middle + i);
            }
        }
    }
}
