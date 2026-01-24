package com.liquidpixel.main.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.FollowComponent;
import com.liquidpixel.main.utils.Mappers;

public class FollowSystem extends IteratingSystem {

    private ComponentMapper<PositionComponent> pm = Mappers.position;
    private ComponentMapper<FollowComponent> fm = Mappers.follow;

    public FollowSystem() {
        super(Family.all(FollowComponent.class, PositionComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        FollowComponent follow = fm.get(entity);
        PositionComponent entityPosition = pm.get(entity);
        PositionComponent targetPosition = pm.get(follow.getFollowTarget());
        entityPosition.setPosition(targetPosition.getPosition().add(follow.getOffset()));
    }
}
