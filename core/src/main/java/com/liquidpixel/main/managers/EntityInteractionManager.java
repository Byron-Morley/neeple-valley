package com.liquidpixel.main.managers;

import com.badlogic.ashley.core.Entity;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages Z-ordering for specific entity interactions
 */
public class EntityInteractionManager {
    // Singleton instance
    private static EntityInteractionManager instance;

    // Maps an entity to another entity that should render above it
    private Map<Entity, Entity> interactionMap = new HashMap<>();

    private EntityInteractionManager() {}

    public static EntityInteractionManager getInstance() {
        if (instance == null) {
            instance = new EntityInteractionManager();
        }
        return instance;
    }

    /**
     * Register that topEntity should render above bottomEntity during their interaction
     */
    public void registerInteraction(Entity bottomEntity, Entity topEntity) {
        interactionMap.put(bottomEntity, topEntity);
    }

    /**
     * Remove the interaction between these entities
     */
    public void removeInteraction(Entity bottomEntity) {
        interactionMap.remove(bottomEntity);
    }

    /**
     * Check if entity1 should render above entity2 based on registered interactions
     * @return true if entity1 should render above entity2
     */
    public boolean shouldRenderAbove(Entity entity1, Entity entity2) {
        // Check if entity1 is registered to render above entity2
        return interactionMap.get(entity2) == entity1;
    }
}
