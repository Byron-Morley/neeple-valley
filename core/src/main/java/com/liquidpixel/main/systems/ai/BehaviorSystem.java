package com.liquidpixel.main.systems.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.liquidpixel.main.components.ai.BehaviorComponent;
import com.liquidpixel.main.components.ai.BehavioringComponent;
import com.liquidpixel.main.interfaces.managers.IBehaviorManager;

public class BehaviorSystem extends IntervalIteratingSystem {
    private final static float INTERVAL = 0.1f;
    IBehaviorManager behaviorManager;

    public BehaviorSystem(IBehaviorManager behaviorManager) {
        super(Family.all(BehaviorComponent.class).exclude(BehavioringComponent.class).get(), INTERVAL);
        this.behaviorManager = behaviorManager;
    }

    @Override
    protected void processEntity(Entity agent) {
        behaviorManager.activate(agent);
    }
}
