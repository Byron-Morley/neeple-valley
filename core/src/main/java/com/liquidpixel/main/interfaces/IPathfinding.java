package com.liquidpixel.main.interfaces;

import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.ai.pathfinding.PathFinderResult;


public interface IPathfinding {
    PathFinderResult searchNodePath(GridPoint2 gridPoint2, GridPoint2 gridPoint21);
    boolean hasValidPath(GridPoint2 from, GridPoint2 to);
}
