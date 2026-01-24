package com.liquidpixel.main.scenarios;

import com.liquidpixel.main.interfaces.IScenario;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.ScenarioState;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.pathfinding.api.IMapService;

import java.util.Arrays;
import java.util.List;

public class ClothesScenario extends Scenario implements IScenario {
    public ClothesScenario(IMapService mapService, IWorldMap worldMap, ISelectionService selectionService, ISettlementService settlementService, IAgentService agentService, IItemService itemService, IStorageService storageService) {
        super(mapService, worldMap, selectionService, settlementService, agentService, itemService, storageService);
    }

    @Override
    public void start() {
        // Default to the hair color demo
        loadHairDemoState();
    }

    @Override
    public void reset() {
        clearAllSpawnedEntities();
    }

    @Override
    public List<ScenarioState> getAvailableStates() {
        return Arrays.asList(
            new ScenarioState("hair_demo", "Hair Color Ramp Demo")
            );
    }

    @Override
    public boolean loadState(String stateId) {
        // Clear current state first
        reset();

        switch (stateId) {
            case "hair_demo":
                loadHairDemoState();
                return true;
            default:
                System.out.println("Unknown state: " + stateId);
                return false;
        }
    }

    private void loadHairDemoState() {

    }

}
