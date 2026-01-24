package com.liquidpixel.main.systems.inits;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.liquidpixel.main.components.ClickableComponent;
import com.liquidpixel.main.components.selection.SelectableEntityComponent;
import com.liquidpixel.main.components.inits.ClickBehaviorInitComponent;
import com.liquidpixel.selection.api.IClickBehaviorService;

/**
 * Adds listeners to newly created items
 */
public class ClickBehaviorInitSystem extends IteratingSystem {

    IClickBehaviorService clickBehaviorService;

    public ClickBehaviorInitSystem(IClickBehaviorService clickBehaviorService) {
        super(Family.all(ClickBehaviorInitComponent.class).exclude(SelectableEntityComponent.class, ClickableComponent.class).get());
        this.clickBehaviorService = clickBehaviorService;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ClickBehaviorInitComponent clickBehaviorInitComponent = entity.getComponent(ClickBehaviorInitComponent.class);
        String behaviorId = clickBehaviorInitComponent.getBehaviorId();

        entity.add(new ClickableComponent(clickBehaviorService.createClickBehavior(behaviorId), behaviorId));
        entity.add(new SelectableEntityComponent());

        entity.remove(ClickBehaviorInitComponent.class);
    }
}

