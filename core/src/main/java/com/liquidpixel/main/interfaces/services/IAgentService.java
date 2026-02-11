package com.liquidpixel.main.interfaces.services;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.factories.AgentFactory;

import java.util.List;

public interface IAgentService {
    Entity spawnAgent(GridPoint2 location);
    Entity spawnAgent(GridPoint2 location, String type);
    AgentFactory getAgentFactory();
    Entity createAgent(String type);
    List<Entity> getAllAgents();
    Entity getGhostAgent(String name);
}
