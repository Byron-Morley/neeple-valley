package com.liquidpixel.main.scenarios;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.interfaces.IScenario;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.ScenarioState;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.main.services.FarmService;
import com.liquidpixel.pathfinding.api.IMapService;

import java.util.Arrays;
import java.util.List;

public class FarmingScenario extends Scenario implements IScenario {

    public FarmingScenario(IMapService mapService, IWorldMap worldMap, ISelectionService selectionService,
                           ISettlementService settlementService, IAgentService agentService, IItemService itemService, IStorageService storageService) {
        super(mapService, worldMap, selectionService, settlementService, agentService, itemService, storageService);
    }

    @Override
    public void start() {
        Entity person = agentService.spawnAgent(new GridPoint2(12, 12), "population");
        trackEntity(person);

        Entity building = itemService.getItem("providers/farm").build();
        itemService.spawnItem(building, new GridPoint2(13, 13));
        trackEntity(building);
    }

    @Override
    public void reset() {
        clearAllSpawnedEntities();
    }

    @Override
    public List<ScenarioState> getAvailableStates() {
        return Arrays.asList(
            new ScenarioState("initial", "Initial State"),
            new ScenarioState("Buying_a_Farm", "Buying a Farm"),
            new ScenarioState("Farming", "Farming")
        );
    }

    @Override
    public boolean loadState(String stateId) {
        // Clear current state first
        reset();

        switch (stateId) {
            case "initial":
                loadInitialState();
                return true;
            case "Buying_a_Farm":
                loadBuyingAFarmState();
                return true;
            case "Farming":
                loadFarmingState();
                return true;
            default:
                System.out.println("Unknown state: " + stateId);
                return false;
        }
    }

    private void loadBuyingAFarmState() {
    }

    private void loadFarmingState() {
        Entity person = agentService.spawnAgent(new GridPoint2(12, 12), "population");
        trackEntity(person);

        Entity building = itemService.getItem("providers/farm").build();
        itemService.spawnItem(building, new GridPoint2(13, 13));
        trackEntity(building);

        FarmService farmService = new FarmService(itemService);
        farmService.plantCropInFarm("farmables/radish", building);


        System.out.println("FarmingState");
    }

    private void loadInitialState() {
        start();
    }
}
