package com.liquidpixel.main.services;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.builder.AgentBuilder;
import com.liquidpixel.main.components.*;
import com.liquidpixel.main.components.agent.AgentComponent;
import com.liquidpixel.main.factories.AgentFactory;
import com.liquidpixel.main.generators.map.MapConfig;
import com.liquidpixel.main.interfaces.services.IAgentService;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.main.utils.shape.Shape;

import java.util.LinkedList;
import java.util.List;

public class AgentService extends Service implements IAgentService {
    protected ComponentMapper<PositionComponent> pm = Mappers.position;
    protected AgentFactory agentFactory;

    public AgentService(AgentFactory agentFactory) {
        this.agentFactory = agentFactory;
    }

    public List<Entity> in(Shape area) {
        List<Entity> entities = new LinkedList();
        ImmutableArray<Entity> allEntities = getEngine().getEntitiesFor(Family.all(PositionComponent.class).get());

        for (Entity e : allEntities) {
            PositionComponent entityPosition = pm.get(e);

            if (area.contains(entityPosition.getX(), entityPosition.getY()))
                entities.add(e);
        }
        return entities;
    }

    public AgentBuilder get(String id) {
        return agentFactory.create(id);
    }

    public Entity spawnAgent(GridPoint2 location) {
        return this.spawnAgent(location, "population");
    }

    public static void printEntityComponents(Entity entity) {
        ImmutableArray<Component> components = entity.getComponents();

        for (Component component : components) {
            Gdx.app.debug("ENTITY", component.getClass().getSimpleName());
        }
    }

    public Entity getAgent(String type){
        AgentBuilder agentBuilder = agentFactory.create(type);
        Entity agentEntity = agentBuilder.build();
        printEntityComponents(agentEntity);
        getEngine().addEntity(agentEntity);
        return agentEntity;
    }

    public Entity spawnAgent(GridPoint2 location, String type) {
        AgentBuilder agentBuilder = agentFactory.create(type).at(location.x, location.y);
        Entity agentEntity = agentBuilder.build();
        printEntityComponents(agentEntity);

        getEngine().addEntity(agentEntity);
        return agentEntity;
    }

    public Entity getAgentById(String id) {
        ImmutableArray<Entity> allEntities = getEngine().getEntitiesFor(Family.all(AgentComponent.class).get());
        for (Entity entity : allEntities) {
            AgentComponent agentComponent = Mappers.agent.get(entity);
            if (agentComponent.getId().equals(id)) {
                return entity;
            }
        }
        return null;
    }

    public AgentFactory getAgentFactory() {
        return agentFactory;
    }

    @Override
    public List<Entity> getAllAgents() {
        List<Entity> agents = new LinkedList<>();
        ImmutableArray<Entity> allEntities = getEngine().getEntitiesFor(Family.all(AgentComponent.class).get());

        for (Entity entity : allEntities) {
            agents.add(entity);
        }

        return agents;
    }

    @Override
    public Entity getGhostAgent(String name) {
        return getAgentFactory().getGhostAgent(name).build();
    }
}
