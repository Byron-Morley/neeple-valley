package com.liquidpixel.main.interfaces.managers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.liquidpixel.main.ai.blackboards.BehaviorBlackboard;
import com.liquidpixel.main.interfaces.services.IBehaviorService;

import java.util.Map;

public interface IBehaviorManager {

    void activate(Entity entity);

    void tick(float scaledDelta);

    IBehaviorService getBehaviorService();

    Map<Entity, BehaviorTree<BehaviorBlackboard>> getBehaviors();
}
