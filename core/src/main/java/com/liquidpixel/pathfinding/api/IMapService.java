package com.liquidpixel.pathfinding.api;

import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.ai.pathfinding.PathFinderResult;
import com.liquidpixel.main.generators.map.WorldMap;
import com.liquidpixel.main.interfaces.ITerrainItemService;
import com.liquidpixel.main.services.PathfindingService;

import java.util.List;

public interface IMapService {
    List<GridPoint2> getWalkableAdjacentNodes(int x, int y, int width, int height);
    GridPoint2 findValidInteractionPoint(GridPoint2 from, GridPoint2 to);
    WorldMap getWorldMap();
    ITerrainItemService getTerrainItemService();
    PathfindingService getPathfindingService();
    boolean isInHomeArea(GridPoint2 position);
    void reset();
    List<GridPoint2> buildWayPoints(GridPoint2 from, PathFinderResult pathFinderResult);
    boolean isWalkableNode(GridPoint2 position);
}
