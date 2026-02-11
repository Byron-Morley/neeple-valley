package com.liquidpixel.main.scenarios;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.components.tasks.FishingComponent;
import com.liquidpixel.main.interfaces.IScenario;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.ScenarioState;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.pathfinding.api.IMapService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ActionScenario extends Scenario implements IScenario {

    public ActionScenario(IMapService mapService, IWorldMap worldMap, ISelectionService selectionService,
                          ISettlementService settlementService, IAgentService agentService, IItemService itemService) {
        super(mapService, worldMap, selectionService, settlementService, agentService, itemService);
    }

    @Override
    public void start() {
        System.out.println("TaskScenario started");
        new ScenarioBuilder().withRiver().build(worldMap, mapService);
    }

    @Override
    public void reset() {
        clearAllSpawnedEntities();
        System.out.println("TaskScenario reset");
    }

    @Override
    public List<ScenarioState> getAvailableStates() {
        return Arrays.asList(
            new ScenarioState("fishing", "Fishing"),
            new ScenarioState("advanced", "Advanced State")
        );
    }

    @Override
    public boolean loadState(String stateId) {
        try {
            // Find a public method with the given name that takes no parameters
            Method method = this.getClass().getMethod(stateId);
            // Invoke the method on the current instance of this class
            method.invoke(this);
            return true;
        } catch (NoSuchMethodException e) {
            System.err.println("Could not find a public method named '" + stateId + "' with no arguments.");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            System.err.println("The method '" + stateId + "' threw an exception.");
            throw new RuntimeException(e.getTargetException());
        } catch (IllegalAccessException e) {
            System.err.println("Could not access the method '" + stateId + "'. Is it public?");
            throw new RuntimeException(e);
        }
        return false;
    }

    public void fishing() {
        System.out.println("Loading fishing state...");

        Entity person = agentService.spawnAgent(new GridPoint2(16, 16), "man");
        trackEntity(person);

        SettlementComponent settlement = Mappers.settlement.get(selectionService.getSelectedSettlement());
        settlement.addPopulation(person);

        person.add(new FishingComponent());

    }

    public void advanced() {
        System.out.println("Loading advanced state...");
        // Add more complex setup here
    }
}
