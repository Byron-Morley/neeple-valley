package com.liquidpixel.main.scenarios;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.core.core.Status;
import com.liquidpixel.main.components.agent.AgentComponent;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.components.tasks.FishingComponent;
import com.liquidpixel.main.interfaces.IScenario;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.ScenarioState;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.sprite.api.services.IAnimationService;
import com.liquidpixel.sprite.services.AnimationService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ToolScenario extends Scenario implements IScenario {

    Entity agent;
    IAnimationService animationService;

    public ToolScenario(IMapService mapService, IWorldMap worldMap, ISelectionService selectionService,
                        ISettlementService settlementService, IAgentService agentService, IItemService itemService, IStorageService storageService) {
        super(mapService, worldMap, selectionService, settlementService, agentService, itemService, storageService);
    }

    @Override
    public void start() {
        System.out.println("ExampleScenario started");
        new ScenarioBuilder().build(worldMap, mapService);

        agent = agentService.spawnAgent(new GridPoint2(16, 16), "man");
        trackEntity(agent);

        SettlementComponent settlement = Mappers.settlement.get(selectionService.getSelectedSettlement());
        settlement.addPopulation(agent);
        animationService = new AnimationService(agent);

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
        System.out.println("TaskScenario reset");
    }

    @Override
    public List<ScenarioState> getAvailableStates() {
        return Arrays.asList(
            new ScenarioState("fishing", "Fishing")
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

        //get the correct tool
        Entity toolEntity = itemService.getItem("tools/axe").build();
        itemService.spawnItem(toolEntity, new GridPoint2(10, 10));


        IAnimationService anim = new AnimationService(toolEntity);
        anim.setAnimation(new Status("CHOP_DOWN"));


//        Mappers.equipment.get(agent).addEquipment(toolEntity);

        //equip the tool
//        AgentComponent agentComponent = Mappers.agent.get(agent);
//        agentComponent.setEquipped(toolEntity);

//        animationService.setAnimation(new Status("CHOP_DOWN"));
        System.out.println("Fishing State loaded - Agent positioned to fish");
    }

    public void advanced() {
        System.out.println("Loading advanced state...");
        // Add more complex setup here
    }
}
