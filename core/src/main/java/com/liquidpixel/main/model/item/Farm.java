package com.liquidpixel.main.model.item;

import com.badlogic.gdx.math.GridPoint2;

import java.util.List;

public class Farm {
    GridPoint2 origin;
    public List<GridPoint2> plots;

    public GridPoint2 getOrigin() {
        return origin;
    }

    public void setOrigin(GridPoint2 origin) {
        this.origin = origin;
    }


    public List<GridPoint2> getPlots() {
        return plots;
    }

    public void setPlots(List<GridPoint2> plots) {
        this.plots = plots;
    }
}
