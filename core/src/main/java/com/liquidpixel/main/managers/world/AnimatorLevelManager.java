package com.liquidpixel.main.managers.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.interfaces.services.IAgentService;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.managers.core.LevelManager;

public class AnimatorLevelManager implements LevelManager {

    IAgentService agentService;
    IItemService itemService;
    Entity playerEntity;

    public AnimatorLevelManager(IAgentService agentService, IItemService itemService) {
        this.agentService = agentService;
        this.itemService = itemService;
    }

    @Override
    public void init() {
        System.out.println("Initializing Animator Level Manager");
        playerEntity = agentService.spawnAgent(new GridPoint2(0, 0), "population");
    }

    public Entity getPlayerEntity() {
        return playerEntity;
    }
}
