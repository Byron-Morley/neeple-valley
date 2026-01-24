package com.liquidpixel.main.listeners;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.liquidpixel.main.components.BodyComponent;
import com.liquidpixel.main.utils.Mappers;

import java.util.HashMap;
import java.util.Map;

/**
 * This is used to track entities being added and removed from the engine.
 */

public class PhysicsEntitiesListener implements EntityListener {
    private ComponentMapper<BodyComponent> bm = Mappers.body;

    private World world;
    private Map<Entity, Body> entitiesToBodies;

    public PhysicsEntitiesListener(World world) {
        this.entitiesToBodies = new HashMap<>();
//        this.world = world;
    }

    @Override
    public void entityAdded(Entity entity) {

        //TODO this needs to be re-purposed to add the body to the pathing nodes

//        BodyComponent bodyComponent = bm.get(entity);
//        entitiesToBodies.put(entity, bodyComponent.body);



    }

    @Override
    public void entityRemoved(Entity entity) {
        world.destroyBody(entitiesToBodies.get(entity));
    }
}
