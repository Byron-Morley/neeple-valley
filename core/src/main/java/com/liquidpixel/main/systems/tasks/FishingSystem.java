package com.liquidpixel.main.systems.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.liquidpixel.main.components.tasks.FishingComponent;

public class FishingSystem extends IteratingSystem {

    public FishingSystem() {
        super(Family.all(FishingComponent.class).get());
    }

    @Override
    protected void processEntity(Entity agent, float deltaTime) {
        FishingComponent component = agent.getComponent(FishingComponent.class);

        switch (component.state) {
            case IDLE:
                handleIdleState(agent, component);
                break;
        }
    }

    private void handleIdleState(Entity agent, FishingComponent component) {
        // can we find a water tile
        // how do we even do this?
        // probably need a new method
        // are we allowing fishing anywhere?
    }
}
