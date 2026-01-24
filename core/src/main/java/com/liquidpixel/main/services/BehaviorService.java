package com.liquidpixel.main.services;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.liquidpixel.main.ai.blackboards.BehaviorBlackboard;
import com.liquidpixel.main.interfaces.managers.IBehaviorManager;
import com.liquidpixel.main.interfaces.services.IBehaviorService;

public class BehaviorService extends Service implements IBehaviorService {

    IBehaviorManager behaviorManager;

    public BehaviorService(IBehaviorManager behaviorManager) {
        this.behaviorManager = behaviorManager;
    }

    @Override
    public BehaviorTree<BehaviorBlackboard> getBehavior(Entity entity) {
        return behaviorManager.getBehaviors().get(entity);
    }
}
