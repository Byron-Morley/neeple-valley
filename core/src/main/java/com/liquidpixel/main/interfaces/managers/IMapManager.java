package com.liquidpixel.main.interfaces.managers;

import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.services.PathfindingService;

public interface IMapManager {
    IMapService getMapService();

    PathfindingService getPathfindingService();

    IWorldMap getWorldMap();

    void reset();
}
