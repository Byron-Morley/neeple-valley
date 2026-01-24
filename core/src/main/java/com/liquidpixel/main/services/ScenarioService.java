package com.liquidpixel.main.services;

import com.liquidpixel.main.interfaces.IScenario;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.main.scenarios.*;
import com.liquidpixel.pathfinding.api.IMapService;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScenarioService implements IScenarioService {

    private final IMapService mapService;
    private final IWorldMap worldMap;
    private final ISelectionService selectionService;
    private final ISettlementService settlementService;
    private final IAgentService agentService;
    private final IItemService itemService;
    private final IStorageService storageService;

    private IScenario currentScenario;
    private final Map<String, Class<? extends IScenario>> scenarioClasses = new HashMap<>();

    public ScenarioService(IMapService mapService, IWorldMap worldMap, ISelectionService selectionService,
                          ISettlementService settlementService, IAgentService agentService, IItemService itemService, IStorageService storageService) {
        this.mapService = mapService;
        this.worldMap = worldMap;
        this.selectionService = selectionService;
        this.settlementService = settlementService;
        this.agentService = agentService;
        this.itemService = itemService;
        this.storageService = storageService;

        initializeScenarioRegistry();
    }

    private void initializeScenarioRegistry() {
        scenarioClasses.put("ActionScenario", ActionScenario.class);
//        scenarioClasses.put("ProcGenScenario", ProcGenScenario.class);
//        scenarioClasses.put("DoorScenario", DoorScenario.class);
//        scenarioClasses.put("BuildingScenario", BuildingScenario.class);
//        scenarioClasses.put("FarmingScenario", FarmingScenario.class);
//        scenarioClasses.put("AgentColorScenario", AgentColorScenario.class);
//        scenarioClasses.put("WoodCutter", WoodCutterScenario.class);
//        scenarioClasses.put("Selection", SelectionScenario.class);
//        scenarioClasses.put("Haul", HaulScenario.class);
    }

    @Override
    public List<String> getAvailableScenarios() {
        return new ArrayList<>(scenarioClasses.keySet());
    }

    @Override
    public IScenario loadScenario(String scenarioName) {
        clearCurrentScenario();

        Class<? extends IScenario> scenarioClass = scenarioClasses.get(scenarioName);
        if (scenarioClass == null) {
            throw new IllegalArgumentException("Unknown scenario: " + scenarioName);
        }

        try {
            // Use reflection to create the scenario instance dynamically
            Constructor<? extends IScenario> constructor = scenarioClass.getConstructor(
                IMapService.class,
                IWorldMap.class,
                ISelectionService.class,
                ISettlementService.class,
                IAgentService.class,
                IItemService.class,
                IStorageService.class
            );

            currentScenario = constructor.newInstance(
                mapService,
                worldMap,
                selectionService,
                settlementService,
                agentService,
                itemService,
                storageService
            );

            return currentScenario;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create scenario: " + scenarioName, e);
        }
    }

    @Override
    public void startCurrentScenario() {
        if (currentScenario != null) {
            currentScenario.start();
        }
    }

    @Override
    public void resetCurrentScenario() {
        if (currentScenario != null) {
            currentScenario.reset();
        }
    }

    @Override
    public void clearCurrentScenario() {
        if (currentScenario != null) {
            currentScenario.reset();
            currentScenario = null;
        }
    }

    @Override
    public IScenario getCurrentScenario() {
        return currentScenario;
    }

    @Override
    public boolean hasCurrentScenario() {
        return currentScenario != null;
    }
}
