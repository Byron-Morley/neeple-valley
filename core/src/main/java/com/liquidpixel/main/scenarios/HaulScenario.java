package com.liquidpixel.main.scenarios;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.interfaces.IScenario;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.ScenarioState;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.main.services.items.StorageHelper;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.pathfinding.api.IMapService;

import java.util.Arrays;
import java.util.List;


public class HaulScenario extends Scenario implements IScenario {

    public HaulScenario(IMapService mapService, IWorldMap worldMap, ISelectionService selectionService, ISettlementService settlementService, IAgentService agentService, IItemService itemService) {
        super(mapService, worldMap, selectionService, settlementService, agentService, itemService);
    }

    @Override
    public void start() {
        new ScenarioBuilder().build(worldMap, mapService);
    }

    @Override
    public void reset() {
        clearAllSpawnedEntities();
    }

    @Override
    public List<ScenarioState> getAvailableStates() {
        return Arrays.asList(
            new ScenarioState("initial", "Initial State"),
            new ScenarioState("floor_to_floor_storage", "Floor to Floor Storage"),
            new ScenarioState("floor_to_floor_priority_storage", "Floor to Floor Priority Storage"),
            new ScenarioState("floor_storage_to_storage_building", "Floor Storage to storage building"),
            new ScenarioState("storage_building_to_storage_building", "Storage building to storage building")
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
            case "floor_to_floor_storage":
                loadFloorToFloorStorage();
                return true;
            case "floor_to_floor_priority_storage":
                loadFloorToFloorPriorityStorage();
                return true;
            default:
                System.out.println("Unknown state: " + stateId);
                return false;
        }
    }

    private void loadFloorToFloorStorage() {

        Entity person = agentService.spawnAgent(new GridPoint2(16, 16), "population");
        trackEntity(person);

        SettlementComponent settlement = Mappers.settlement.get(selectionService.getSelectedSettlement());
        settlement.addPopulation(person);

        //TODO    Create a new method for creating a storage group correctly for testing

        Entity storage = StorageHelper.createGroupStorage(new GridPoint2(20, 20), settlementService, 1, 1);

        settlementService.buildInSettlement("resources/wood_log", new GridPoint2(30, 40), 100);
    }


    private void loadFloorToFloorPriorityStorage() {

        SettlementComponent settlement = Mappers.settlement.get(selectionService.getSelectedSettlement());

        Entity person = agentService.spawnAgent(new GridPoint2(16, 16), "population");
        trackEntity(person);
        settlement.addPopulation(person);


//        Entity person2 = agentService.spawnAgent(new GridPoint2(18, 16), "population");
//        trackEntity(person2);
//        settlement.addPopulation(person2);



        Entity storage1 = StorageHelper.createGroupStorage(new GridPoint2(20, 20), settlementService, 2, 2);
        Entity storage2 = StorageHelper.createGroupStorage(new GridPoint2(27, 20), settlementService, 2, 2);

//        storageService.setPriority(storage1, 2);
        StorageHelper.setPriority(storage2, 2);

        settlementService.buildInSettlement("resources/wood_log", new GridPoint2(15, 30), 30);
        settlementService.buildInSettlement("resources/stone", new GridPoint2(18, 28), 30);
        settlementService.buildInSettlement("resources/wheat", new GridPoint2(12, 22), 30);
        settlementService.buildInSettlement("resources/iron_ore", new GridPoint2(9, 12), 30);
    }

    private void loadInitialState() {
        start();
    }
}
