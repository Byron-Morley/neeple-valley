package com.liquidpixel.main.ai.blackboards;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.ai.behavior.stack.Value;
import com.liquidpixel.main.ai.blackboards.Blackboard;
import com.liquidpixel.main.ai.tasks.PoolableTaskManager;
import com.liquidpixel.main.components.agent.AgentJobComponent;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.interfaces.*;
import com.liquidpixel.main.interfaces.managers.ITaskManager;
import com.liquidpixel.main.interfaces.services.IAgentService;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.main.managers.core.BehaviorManager;
import com.liquidpixel.main.model.ai.Job;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.main.utils.predicates.Sorter;

import java.util.*;

public class BehaviorBlackboard extends Blackboard implements IBehaviorBlackboard {

    public static class Haul {
        Entity entity;
        String itemName;

        public Haul(Entity entity, String itemName) {
            this.entity = entity;
            this.itemName = itemName;
        }

        public Entity getEntity() {
            return entity;
        }

        public String getItemName() {
            return itemName;
        }
    }

    ITaskManager taskManager;

    Entity entity;
    Engine engine;
    IMapService mapService;
    BehaviorManager behaviorManager;
    IAgentService agentService;
    IItemService itemService;
    public Stack<Value> stack;
    public Set<GridPoint2> blackList;

    public BehaviorBlackboard(
        IMapService mapService,
        BehaviorManager behaviorManager,
        Entity entity,
        IAgentService agentService,
        IItemService itemService
    ) {
        super();
        this.entity = entity;
        this.mapService = mapService;
        this.behaviorManager = behaviorManager;
        this.agentService = agentService;
        this.itemService = itemService;
        this.engine = GameResources.get().getEngine();
        stack = new Stack<>();
        blackList = new HashSet<>();
        Gdx.app.debug("blackboard", "blackboard created");
    }

    public Entity getEntity() {
        return entity;
    }

    public IAgentService getAgentService() {
        return agentService;
    }

    public IItemService getItemService() {
        return itemService;
    }

    public Engine getEngine() {
        return engine;
    }

    public GridPoint2 findValidInteractionPoint(GridPoint2 from, GridPoint2 to) {
        return mapService.findValidInteractionPoint(from, to);
    }

    public Job getEntityJob() {
        AgentJobComponent agentJobComponent = entity.getComponent(AgentJobComponent.class);
        if (agentJobComponent == null) {
            System.out.println("No job for this agent");
            return null;
        }
        return null;
    }

    public Entity getWorkshop() {
        return getEntityJob().getWorkshop();
    }

    public Entity findEntity(List<Entity> entities, String itemId) {
        for (Entity entity : entities) {
            ItemComponent itemComponent = Mappers.item.get(entity);
            if (itemComponent != null && itemComponent.getName().equals(itemId)) {
                return entity;
            }
        }
        return null;
    }

    public Set<GridPoint2> getBlackList() {
        return blackList;
    }

    public void setBlackList(Set<GridPoint2> blackList) {
        this.blackList = blackList;
    }

    @Override
    public PoolableTaskManager getDomainTaskManager() {
        return (PoolableTaskManager) taskManager;
    }

    public List<Entity> getListOfPotentialEntitiesToPickup() {
        Entity entity = getEntity();
        IItemService itemService = getItemService();
        Vector2 agentPosition = Mappers.position.get(entity).getPosition();
        Sorter sorter = new Sorter(agentPosition);

        return itemService
            .getPickuableItems()
            .stream()
            .sorted(sorter::sortByCloseness)
            .toList();
    }

    public Haul getSuitableEntityToHaul(List<Entity> entities, Map<String, StorageItem> requiredResources) {

        for (Entity entity : entities) {

            ItemComponent itemComponent = Mappers.item.get(entity);
            String itemId = itemComponent.getName();
            StorageComponent storageComponent = Mappers.storage.get(entity);

            if (!getBlackList().contains(Mappers.position.get(entity).getGridPosition())) {
                if (storageComponent == null) {
                    if (requiredResources.containsKey(itemId)) return new Haul(entity, itemId);
                } else {
//                    String matchingKey = findMatchingItemKey(storageComponent.getItems(), requiredResources);
//                    if (matchingKey != null) return new Haul(entity, matchingKey);
                }
            }
        }
        return null;
    }

    public String findMatchingItemKey(Map<String, Entity> items, Map<String, StorageItem> requestedItems) {
        for (Map.Entry<String, Entity> entry : items.entrySet()) {
            String key = entry.getKey();
            if (requestedItems.containsKey(key)) {
                return key;
            }
        }
        return null;
    }
}
