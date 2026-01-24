package com.liquidpixel.main.ai.pathfinding;

import com.badlogic.gdx.math.GridPoint2;

public class Path {
    GridPoint2 start;
    GridPoint2 end;

    public Path(GridPoint2 start, GridPoint2 end) {
        this.start = start;
        this.end = end;
    }

    public GridPoint2 getStart() {
        return start;
    }

    public void setStart(GridPoint2 start) {
        this.start = start;
    }

    public GridPoint2 getEnd() {
        return end;
    }

    public void setEnd(GridPoint2 end) {
        this.end = end;
    }

    public String toString() {
        return "Start: " + start + " End: " + end + "\n";
    }
}
