package com.liquidpixel.main.scenarios;

import com.liquidpixel.main.interfaces.IScenario;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.ScenarioState;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.pathfinding.api.IMapService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ExampleScenario extends Scenario implements IScenario {

    public ExampleScenario(IMapService mapService, IWorldMap worldMap, ISelectionService selectionService,
                           ISettlementService settlementService, IAgentService agentService, IItemService itemService) {
        super(mapService, worldMap, selectionService, settlementService, agentService, itemService);
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

    private void basic() {
        System.out.println("Loading basic state...");
        // Add basic entities here
    }

    private void advanced() {
        System.out.println("Loading advanced state...");
        // Add more complex setup here
    }
}
