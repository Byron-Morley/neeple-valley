package com.liquidpixel.main.interfaces.services;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.liquidpixel.main.ai.blackboards.BehaviorBlackboard;

public interface IBehaviorService {
    BehaviorTree<BehaviorBlackboard> getBehavior(Entity entity);
}
