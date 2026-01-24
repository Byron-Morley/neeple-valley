package com.liquidpixel.main.scenarios;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.DoorComponent;
import com.liquidpixel.main.components.colony.EnterDoorComponent;
import com.liquidpixel.main.components.colony.ExitDoorComponent;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.interfaces.IScenario;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.ScenarioState;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.pathfinding.api.IMapService;

import java.util.Arrays;
import java.util.List;

public class DoorScenario extends Scenario implements IScenario {

    public DoorScenario(IMapService mapService, IWorldMap worldMap, ISelectionService selectionService,
                        ISettlementService settlementService, IAgentService agentService, IItemService itemService, IStorageService storageService) {
        super(mapService, worldMap, selectionService, settlementService, agentService, itemService, storageService);
    }

    @Override
    public void start() {
        Entity person = agentService.spawnAgent(new GridPoint2(16, 16), "population");
        trackEntity(person);

        SettlementComponent settlement = Mappers.settlement.get(selectionService.getSelectedSettlement());
        settlement.addPopulation(person);

        Entity door = itemService.getItem("houses/door").build();
        itemService.spawnItem(door, new GridPoint2(10, 10));
        trackEntity(door);

        DoorComponent doorComponent = Mappers.door.get(door);
        doorComponent.addOccupant(person);
        person.add(new ExitDoorComponent(door));
    }

    @Override
    public void reset() {
        clearAllSpawnedEntities();
    }

    @Override
    public List<ScenarioState> getAvailableStates() {
        return Arrays.asList(
            new ScenarioState("initial", "Initial State"),
            new ScenarioState("enter_door", "Enter Door Test"),
            new ScenarioState("exit_door", "Exit Door Test"),
            new ScenarioState("multiple_agents", "Multiple Agents Test")
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
            case "enter_door":
                loadEnterDoorState();
                return true;
            case "exit_door":
                loadExitDoorState();
                return true;
            case "multiple_agents":
                loadMultipleAgentsState();
                return true;
            default:
                System.out.println("Unknown state: " + stateId);
                return false;
        }
    }

    private void loadInitialState() {
        // This is the same as the original start() method
        start();
    }

    private void loadEnterDoorState() {
        // Create door first
        Entity door = itemService.getItem("houses/door").build();
        itemService.spawnItem(door, new GridPoint2(10, 10));
        trackEntity(door);

        // Create agent positioned to enter the door
        Entity person = agentService.spawnAgent(new GridPoint2(9, 10), "population");
        trackEntity(person);

        SettlementComponent settlement = Mappers.settlement.get(selectionService.getSelectedSettlement());
        settlement.addPopulation(person);

        // Add component to trigger door entry
        person.add(new EnterDoorComponent(door));

        System.out.println("Enter Door State loaded - Agent positioned to enter door");
    }

    private void loadExitDoorState() {
        // Create door first
        Entity door = itemService.getItem("houses/door").build();
        itemService.spawnItem(door, new GridPoint2(10, 10));
        trackEntity(door);

        // Create agent positioned inside the door
        Entity person = agentService.spawnAgent(new GridPoint2(10, 10), "population");
        trackEntity(person);

        SettlementComponent settlement = Mappers.settlement.get(selectionService.getSelectedSettlement());
        settlement.addPopulation(person);

        // Set up door occupancy and exit component
        DoorComponent doorComponent = Mappers.door.get(door);
        doorComponent.addOccupant(person);
        person.add(new ExitDoorComponent(door));

        System.out.println("Exit Door State loaded - Agent positioned inside door to exit");
    }

    private void loadMultipleAgentsState() {
        // Create door first
        Entity door = itemService.getItem("houses/door").build();
        itemService.spawnItem(door, new GridPoint2(10, 10));
        trackEntity(door);

        SettlementComponent settlement = Mappers.settlement.get(selectionService.getSelectedSettlement());

        // Create agent outside door
        Entity person1 = agentService.spawnAgent(new GridPoint2(9, 10), "population");
        trackEntity(person1);
        settlement.addPopulation(person1);
        person1.add(new EnterDoorComponent(door));

        // Create agent inside door
        Entity person2 = agentService.spawnAgent(new GridPoint2(10, 10), "population");
        trackEntity(person2);
        settlement.addPopulation(person2);
        DoorComponent doorComponent = Mappers.door.get(door);
        doorComponent.addOccupant(person2);
        person2.add(new ExitDoorComponent(door));

        // Create additional agent nearby
        Entity person3 = agentService.spawnAgent(new GridPoint2(8, 10), "population");
        trackEntity(person3);
        settlement.addPopulation(person3);

        System.out.println("Multiple Agents State loaded - Multiple agents testing door interactions");
    }
}
