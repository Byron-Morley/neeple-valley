package com.liquidpixel.selection.api;

import com.badlogic.gdx.math.GridPoint2;

public interface IClickAction {
    IClickAction execute(GridPoint2 position);

    boolean isOneShot();

    IClickAction exit();
}
