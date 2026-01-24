package com.liquidpixel.main.model.building;

import java.util.List;
import java.util.Arrays;

import com.badlogic.gdx.math.GridPoint2;

public enum CaptionPattern {

    ONE_TILE(Arrays.asList(
        new GridPoint2(0, 0)
    )),

    CROSS(Arrays.asList(
        new GridPoint2(1, 0),
        new GridPoint2(0, 1),
        new GridPoint2(-1, 0),
        new GridPoint2(0, -1)
    ));

    private final List<GridPoint2> gridPoints;

    CaptionPattern(List<GridPoint2> gridPoints) {
        this.gridPoints = gridPoints;
    }

    public List<GridPoint2> getGridPoints() {
        return gridPoints;
    }

    public static CaptionPattern getByName(String name) {
        try {
            return valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
