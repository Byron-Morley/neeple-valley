package com.liquidpixel.main.scenarios;

import com.liquidpixel.main.interfaces.IScenario;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.ScenarioState;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.pathfinding.api.IMapService;

import java.util.Arrays;
import java.util.List;


public class ProcGenScenario extends Scenario implements IScenario {

    public ProcGenScenario(IMapService mapService, IWorldMap worldMap, ISelectionService selectionService,
                           ISettlementService settlementService, IAgentService agentService, IItemService itemService) {
        super(mapService, worldMap, selectionService, settlementService, agentService, itemService);
    }

    @Override
    public void start() {
        new ScenarioBuilder()
            .withRiver()
            .withWater()
            .withTerrainItems()
            .build(worldMap, mapService);
    }

    @Override
    public void reset() {
        clearAllSpawnedEntities();
    }

    @Override
    public List<ScenarioState> getAvailableStates() {
        return Arrays.asList(
            new ScenarioState("initial", "Initial State")
        );
    }

    @Override
    public boolean loadState(String stateId) {
        reset();

        switch (stateId) {
            case "initial":
                loadInitialState();
                return true;
            default:
                System.out.println("Unknown state: " + stateId);
                return false;
        }
    }

    private void loadInitialState() {
        start();
    }
}
