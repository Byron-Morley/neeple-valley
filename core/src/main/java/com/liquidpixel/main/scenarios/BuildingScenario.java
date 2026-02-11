package com.liquidpixel.main.scenarios;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.colony.BuildingComponent;
import com.liquidpixel.main.components.colony.EnterDoorComponent;
import com.liquidpixel.main.components.colony.ExitDoorComponent;
import com.liquidpixel.main.interfaces.IScenario;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.ScenarioState;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.pathfinding.api.IMapService;

import java.util.Arrays;
import java.util.List;

/**
 * Example scenario showing how to implement custom states
 * This scenario doesn't actually do anything, it's just a template
 */
public class BuildingScenario extends Scenario implements IScenario {

    public BuildingScenario(IMapService mapService, IWorldMap worldMap, ISelectionService selectionService,
                            ISettlementService settlementService, IAgentService agentService, IItemService itemService) {
        super(mapService, worldMap, selectionService, settlementService, agentService, itemService);
    }

    @Override
    public void start() {


    }

    @Override
    public void reset() {
        clearAllSpawnedEntities();
        System.out.println("ExampleScenario reset");
    }

    @Override
    public List<ScenarioState> getAvailableStates() {
        return Arrays.asList(
            new ScenarioState("exit", "Exit Door"),
            new ScenarioState("enterClose", "Enter Door from Close"),
            new ScenarioState("enterFar", "Enter Door from afar")
        );
    }

    @Override
    public boolean loadState(String stateId) {
        reset(); // Always clear current state first

        switch (stateId) {
            case "exit":
                loadExitState();
                return true;
            case "enterClose":
                loadEnterCloseState();
                return true;
            case "enterFar":
                loadEnterFarState();
                return true;
            default:
                return false;
        }
    }

    private void loadExitState() {
        System.out.println("Loading Exit State...");

        Entity person = agentService.spawnAgent(new GridPoint2(10, 10), "population");
        trackEntity(person);

        Entity building = itemService.getItem("providers/wood-cutter").build();
        itemService.spawnItem(building, new GridPoint2(16, 16));
        trackEntity(building);

        BuildingComponent buildingComponent = Mappers.building.get(building);
        buildingComponent.addOccupant(person);

        person.add(new ExitDoorComponent(building));
    }

     private void loadEnterCloseState() {
        System.out.println("Loading Enter State...");

        Entity person = agentService.spawnAgent(new GridPoint2(20, 16), "population");
        trackEntity(person);

        Entity building = itemService.getItem("providers/wood-cutter").build();
        itemService.spawnItem(building, new GridPoint2(16, 16));
        trackEntity(building);

        person.add(new EnterDoorComponent(building));
    }

    private void loadEnterFarState() {
        System.out.println("Loading Enter State...");

        Entity person = agentService.spawnAgent(new GridPoint2(10, 10), "population");
        trackEntity(person);

        Entity building = itemService.getItem("providers/wood-cutter").build();
        itemService.spawnItem(building, new GridPoint2(16, 16));
        trackEntity(building);

        person.add(new EnterDoorComponent(building));
    }
}
