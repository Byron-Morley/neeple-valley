package com.liquidpixel.main.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.BodyComponent;
import com.liquidpixel.main.utils.Mappers;

public class PhysicsSystem extends IteratingSystem {
    public static final float TIME_STEP = 18f;
    private ComponentMapper<PositionComponent> pm = Mappers.position;
    private ComponentMapper<BodyComponent> bm = Mappers.body;


    public PhysicsSystem() {
        super(Family.all(BodyComponent.class, PositionComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = pm.get(entity);
        BodyComponent body = bm.get(entity);
//
//        Vector2 size = BodyUtils.getBodySize(body.body);
//
//        if (position.overridePhysicsSystem) {
//            position.overridePhysicsSystem = false;
//            Vector2 newPosition = new Vector2(position.getX(), position.getY()).add(size).add(body.getOffset());
//
//            body.body.setTransform(newPosition.x, newPosition.y, body.body.getAngle());
//
//        } else {
//            Vector2 bodyPosition = body.body.getPosition();
//            Vector2 newPosition = new Vector2(bodyPosition.x, bodyPosition.y).sub(size).sub(body.getOffset());
//            position.setPosition(newPosition);
//        }
    }
}
