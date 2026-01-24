package com.liquidpixel.main.systems.render;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.liquidpixel.main.components.render.FadeComponent;
import com.liquidpixel.main.components.render.RenderComponent;
import com.liquidpixel.main.utils.Mappers;

public class FadeSystem extends IteratingSystem {

    public FadeSystem() {
        super(Family.all(FadeComponent.class, RenderComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        FadeComponent fadeComponent = Mappers.fade.get(entity);
        RenderComponent renderComponent = Mappers.render.get(entity);

        // Update the fade component
        fadeComponent.update(deltaTime);

        // Apply the alpha to the render component's color
        renderComponent.getColor().a = fadeComponent.getAlpha();
    }
}
