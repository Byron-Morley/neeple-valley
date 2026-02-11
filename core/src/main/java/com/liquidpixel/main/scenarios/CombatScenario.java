package com.liquidpixel.main.scenarios;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.interfaces.IScenario;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.ScenarioState;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.pathfinding.api.IMapService;

import java.util.Arrays;
import java.util.List;

public class CombatScenario extends Scenario implements IScenario {

    public CombatScenario(IMapService mapService, IWorldMap worldMap, ISelectionService selectionService,
                          ISettlementService settlementService, IAgentService agentService, IItemService itemService) {
        super(mapService, worldMap, selectionService, settlementService, agentService, itemService);
    }

    @Override
    public List<ScenarioState> getAvailableStates() {
        return Arrays.asList(
            new ScenarioState("1", "one on one")
        );
    }

    @Override
    public boolean loadState(String stateId) {
        switch (stateId) {
            case "initial":
                startOneOnOne();
                return true;
            default:
                System.out.println("Unknown state: " + stateId);
                return false;
        }
    }

    private void startOneOnOne() {

        SettlementComponent settlement = Mappers.settlement.get(selectionService.getSelectedSettlement());

        Entity person = agentService.spawnAgent(new GridPoint2(16, 16), "population");
        trackEntity(person);
        settlement.addPopulation(person);
    }

    @Override
    public void start() {
        new ScenarioBuilder().build(worldMap, mapService);
    }

    @Override
    public void reset() {
         new ScenarioBuilder().build(worldMap, mapService);
    }
}
