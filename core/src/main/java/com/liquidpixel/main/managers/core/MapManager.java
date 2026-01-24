package com.liquidpixel.main.managers.core;

import com.liquidpixel.main.ai.pathfinding.MapGraph;
import com.liquidpixel.main.engine.GameState;
import com.liquidpixel.main.generators.map.WorldMap;
import com.liquidpixel.main.interfaces.*;
import com.liquidpixel.main.interfaces.managers.IMapManager;
import com.liquidpixel.main.interfaces.managers.ITaskManager;
import com.liquidpixel.main.interfaces.managers.ITerrainManager;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.main.managers.TerrainManager;
import com.liquidpixel.main.services.MapService;
import com.liquidpixel.main.services.PathfindingService;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;

import java.util.Random;


public class MapManager implements IMapManager {

    public static float TIME_PER_TILE = 2f;

    public static boolean RIVER_ON = true;
    public static boolean TERRAIN_ITEMS_ON = false;
    public static long NON_WATER_SEED = 709748066;
    public static long MUDDY_SEED = -1150544974;
    public static long WATER_SEED = -922278602;



    IWorldMap worldMap;
    ITaskManager taskManager;
    ITerrainManager terrainManager;


    IItemService itemService;
    IMapService mapService;
    PathfindingService pathfindingService;

    Random random;
    int seed;
    Initializable level;

    public MapManager(Initializable level, MapConfiguration mapConfiguration, ITaskManager taskManager, IItemService itemService, ISpriteFactory spriteFactory) {
        this.taskManager = taskManager;
        this.itemService = itemService;
        this.level = level;

        random = new Random();
        seed = random.nextInt();

        //TODO remove seed
        GameState.setSeed(seed);
        System.out.println("Seed: " + seed);

        terrainManager = new TerrainManager();
        worldMap = new WorldMap(mapConfiguration, level, terrainManager.getTerrainService(), spriteFactory);
        pathfindingService = new PathfindingService((MapGraph) worldMap);
        mapService = new MapService(this, taskManager, itemService, terrainManager.getTerrainService(), spriteFactory);
    }

    public IWorldMap getWorldMap() {
        return worldMap;
    }

    @Override
    public void reset() {
        terrainManager.reset();
        ((GameSetup) level).reset();
    }

    public IMapService getMapService() {
        return mapService;
    }

    public PathfindingService getPathfindingService() {
        return pathfindingService;
    }

}
