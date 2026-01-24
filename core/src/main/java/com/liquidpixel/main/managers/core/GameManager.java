package com.liquidpixel.main.managers.core;

import com.liquidpixel.main.interfaces.services.IAgentService;
import com.liquidpixel.main.interfaces.services.IItemService;

public class GameManager implements LevelManager{
    protected IAgentService agentService;
    protected IItemService itemService;

    public GameManager(IAgentService agentService, IItemService itemService) {
        this.agentService = agentService;
        this.itemService = itemService;
    }

    public void init() {
    }
}
