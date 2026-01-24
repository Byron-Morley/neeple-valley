package com.liquidpixel.main.scenarios;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.pathfinding.api.IMapService;

import java.util.ArrayList;
import java.util.List;

public class Scenario {

    protected IMapService mapService;
    protected IWorldMap worldMap;
    protected ISelectionService selectionService;
    protected ISettlementService settlementService;
    protected IAgentService agentService;
    protected IItemService itemService;
    protected IStorageService storageService;
    protected List<Entity> spawnedEntities = new ArrayList<>();

    public Scenario(IMapService mapService, IWorldMap worldMap, ISelectionService selectionService, ISettlementService settlementService, IAgentService agentService, IItemService itemService, IStorageService storageService) {
        this.mapService = mapService;
        this.worldMap = worldMap;
        this.selectionService = selectionService;
        this.settlementService = settlementService;
        this.agentService = agentService;
        this.itemService = itemService;
        this.storageService = storageService;
    }

    protected void trackEntity(Entity entity) {
        spawnedEntities.add(entity);
    }

    protected void clearAllSpawnedEntities() {
        for (Entity entity : spawnedEntities) {
            if (GameResources.get().getEngine().getEntities().contains(entity, true)) {
                GameResources.get().getEngine().removeEntity(entity);
            }
        }
        spawnedEntities.clear();
    }
}
