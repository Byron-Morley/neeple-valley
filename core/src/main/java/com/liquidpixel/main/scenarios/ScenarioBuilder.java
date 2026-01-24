package com.liquidpixel.main.scenarios;

import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.engine.GameState;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.pathfinding.api.IMapService;

import static com.liquidpixel.main.managers.core.MapManager.*;

public class ScenarioBuilder {

    public ScenarioBuilder() {
        RIVER_ON = false;
        GameState.setSeed(NON_WATER_SEED);
        TERRAIN_ITEMS_ON = true;
    }

    public ScenarioBuilder withRiver() {
        RIVER_ON = true;
        return this;
    }

    public ScenarioBuilder withTerrainItems() {
        TERRAIN_ITEMS_ON = true;
        return this;
    }

    public ScenarioBuilder withWater() {
        GameState.setSeed(WATER_SEED);
        return this;
    }

    public ScenarioBuilder withMud(){
        GameState.setSeed(MUDDY_SEED);
        return this;
    }

    public ScenarioBuilder build(IWorldMap worldMap, IMapService mapService) {
        GameResources.get().getEngine().removeAllEntities();
        worldMap.reinit();
        mapService.reset();
        return this;
    }
}
