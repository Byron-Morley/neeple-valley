package com.liquidpixel.main.managers.core;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import com.liquidpixel.main.ai.blackboards.BehaviorBlackboard;
import com.liquidpixel.main.ai.tasks.PoolableTaskManager;
import com.liquidpixel.main.components.ai.BehaviorComponent;
import com.liquidpixel.main.components.ai.BehavioringComponent;
import com.liquidpixel.main.factories.BehaviorFactory;
import com.liquidpixel.main.factories.JobFactory;
import com.liquidpixel.main.interfaces.managers.IBehaviorManager;
import com.liquidpixel.main.interfaces.services.IAgentService;
import com.liquidpixel.main.interfaces.services.IBehaviorService;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.main.services.BehaviorService;
import com.liquidpixel.main.utils.Mappers;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;
import java.util.Map;

public class BehaviorManager extends PoolableTaskManager implements EntityListener, IBehaviorManager {

    ComponentMapper<BehaviorComponent> bm = Mappers.behavior;
    Map<Entity, BehaviorTree<BehaviorBlackboard>> behaviors;

    IMapService mapService;
    IAgentService agentService;
    IItemService itemService;
    IBehaviorService behaviorService;

    JobFactory jobFactory;
    BehaviorFactory behaviorFactory;

    public BehaviorManager(
        IMapService mapService,
        IAgentService agentService,
        IItemService itemService
    ) {
        this.mapService = mapService;
        this.agentService = agentService;
        this.itemService = itemService;
        this.behaviors = new HashMap<>();
        this.jobFactory = new JobFactory();
        this.behaviorFactory = new BehaviorFactory();
        behaviorService = new BehaviorService(this);
    }

    @Override
    public void entityAdded(Entity agent) {
        BehaviorBlackboard blackboard = new BehaviorBlackboard(
            mapService,
            this,
            agent,
            agentService,
            itemService
        );
        behaviors.put(agent, buildBehaviorTree(agent, blackboard));
    }

    private BehaviorTree<BehaviorBlackboard> buildBehaviorTree(Entity agent, BehaviorBlackboard blackboard) {
        BehaviorTreeParser<BehaviorBlackboard> parser = new BehaviorTreeParser<>(BehaviorTreeParser.DEBUG_NONE);
        BehaviorComponent behaviorComponent = bm.get(agent);
        String behaviorFileName = behaviorComponent.getBehaviorFileName();
        FileHandle file = behaviorFactory.get(behaviorFileName);
        return parser.parse(file, blackboard);
    }

    public void activate(Entity agent) {
        agent.add(new BehavioringComponent());
    }

    public void tick(float delta) {
        super.tick(delta);
        for (BehaviorTree<BehaviorBlackboard> behaviorTree : behaviors.values()) {
            behaviorTree.step();
        }
    }

    @Override
    public IBehaviorService getBehaviorService() {
        return behaviorService;
    }

    @Override
    public void entityRemoved(Entity agent) {
        behaviors.remove(agent);
    }

    public void onBehaviorFinished(Entity agent) {
        agent.remove(BehavioringComponent.class);
    }

    public Map<Entity, BehaviorTree<BehaviorBlackboard>> getBehaviors() {
        return behaviors;
    }
}
