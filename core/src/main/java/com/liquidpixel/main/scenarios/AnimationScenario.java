package com.liquidpixel.main.scenarios;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.core.core.Status;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.interfaces.IScenario;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.ScenarioState;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.sprite.api.services.IAnimationService;
import com.liquidpixel.sprite.services.AnimationService;

import java.util.Arrays;
import java.util.List;

/**
 * Example scenario showing how to implement custom states
 * This scenario doesn't actually do anything, it's just a template
 */
public class AnimationScenario extends Scenario implements IScenario {

    Entity person;
    IAnimationService animationService;

    public AnimationScenario(IMapService mapService, IWorldMap worldMap, ISelectionService selectionService,
                             ISettlementService settlementService, IAgentService agentService, IItemService itemService, IStorageService storageService) {
        super(mapService, worldMap, selectionService, settlementService, agentService, itemService, storageService);
    }

    @Override
    public void start() {
        // Basic scenario setup
        System.out.println("ExampleScenario started");
        new ScenarioBuilder().build(worldMap, mapService);

        Entity person = agentService.spawnAgent(new GridPoint2(16, 16), "man");
        trackEntity(person);

        SettlementComponent settlement = Mappers.settlement.get(selectionService.getSelectedSettlement());
        settlement.addPopulation(person);
        animationService = new AnimationService(person);

        animationService.addListener(new AnimationService.AnimationListener() {
            @Override
            public void onFrameChanged(int frameIndex, boolean isFinished) {
                System.out.println("Frame changed: " + frameIndex);
                if (isFinished) animationService.setIdle();
            }
        });

    }

    @Override
    public void reset() {
        clearAllSpawnedEntities();
        System.out.println("ExampleScenario reset");
    }

    @Override
    public List<ScenarioState> getAvailableStates() {
        return Arrays.asList(
            new ScenarioState("standing_up", "Standing Up"),
            new ScenarioState("standing_down", "Standing Down"),
            new ScenarioState("standing_left", "Standing Left"),
            new ScenarioState("standing_right", "Standing Right"),
            new ScenarioState("walking_up", "Walking Up"),
            new ScenarioState("walking_down", "Walking Down"),
            new ScenarioState("walking_left", "Walking Left"),
            new ScenarioState("walking_right", "Walking Right"),
            new ScenarioState("pickup_up", "Pickup Up"),
            new ScenarioState("pickup_down", "Pickup Down"),
            new ScenarioState("pickup_left", "Pickup Left"),
            new ScenarioState("pickup_right", "Pickup Right"),
            new ScenarioState("carrying_up", "Carrying Up"),
            new ScenarioState("carrying_down", "Carrying Down"),
            new ScenarioState("carrying_left", "Carrying Left"),
            new ScenarioState("carrying_right", "Carrying Right"),
            new ScenarioState("holding_up", "Holding Up"),
            new ScenarioState("holding_down", "Holding Down"),
            new ScenarioState("holding_left", "Holding Left"),
            new ScenarioState("holding_right", "Holding Right"),
            new ScenarioState("strike_down", "Strike Down"),
            new ScenarioState("strike_up", "Strike Up"),
            new ScenarioState("strike_left", "Strike Left"),
            new ScenarioState("strike_right", "Strike Right"),
            new ScenarioState("chop_down", "Chop Down"),
            new ScenarioState("chop_up", "Chop Up"),
            new ScenarioState("chop_left", "Chop Left"),
            new ScenarioState("chop_right", "Chop Right"),
            new ScenarioState("cast_down", "Cast Down"),
            new ScenarioState("cast_up", "Cast Up"),
            new ScenarioState("cast_left", "Cast Left"),
            new ScenarioState("cast_right", "Cast Right"),
            new ScenarioState("bite_down", "Bite Down"),
            new ScenarioState("bite_up", "Bite Up"),
            new ScenarioState("bite_left", "Bite Left"),
            new ScenarioState("bite_right", "Bite Right"),
            new ScenarioState("catch_down", "Catch Down"),
            new ScenarioState("catch_up", "Catch Up"),
            new ScenarioState("catch_left", "Catch Left"),
            new ScenarioState("catch_right", "Catch Right")
        );
    }

    @Override
    public boolean loadState(String stateId) {
        try {
            animationService.setAnimation(new Status(stateId.toUpperCase()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
