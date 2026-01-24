package com.liquidpixel.main.scenarios;

import com.liquidpixel.main.interfaces.IScenario;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.ScenarioState;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.pathfinding.api.IMapService;

import java.util.Arrays;
import java.util.List;

/**
 * Example scenario showing how to implement custom states
 * This scenario doesn't actually do anything, it's just a template
 */
public class ExampleScenario extends Scenario implements IScenario {

    public ExampleScenario(IMapService mapService, IWorldMap worldMap, ISelectionService selectionService,
                           ISettlementService settlementService, IAgentService agentService, IItemService itemService, IStorageService storageService) {
        super(mapService, worldMap, selectionService, settlementService, agentService, itemService, storageService);
    }

    @Override
    public void start() {
        // Basic scenario setup
        System.out.println("ExampleScenario started");
    }

    @Override
    public void reset() {
        clearAllSpawnedEntities();
        System.out.println("ExampleScenario reset");
    }

    @Override
    public List<ScenarioState> getAvailableStates() {
        return Arrays.asList(
            new ScenarioState("basic", "Basic State"),
            new ScenarioState("advanced", "Advanced State")
        );
    }

    @Override
    public boolean loadState(String stateId) {
        reset(); // Always clear current state first

        switch (stateId) {
            case "basic":
                loadBasicState();
                return true;
            case "advanced":
                loadAdvancedState();
                return true;
            default:
                return false;
        }
    }

    private void loadBasicState() {
        System.out.println("Loading basic state...");
        // Add basic entities here
    }

    private void loadAdvancedState() {
        System.out.println("Loading advanced state...");
        // Add more complex setup here
    }
}
