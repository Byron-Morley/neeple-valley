package com.liquidpixel.main.interfaces;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.interfaces.services.IAgentService;
import com.liquidpixel.main.interfaces.services.IItemService;

public interface IBehaviorBlackboard {
    IItemService getItemService();
    IAgentService getAgentService();
    Entity getEntity();
    Engine getEngine();
    Entity getWorkshop();
}
