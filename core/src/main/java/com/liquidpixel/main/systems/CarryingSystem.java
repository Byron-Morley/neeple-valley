package com.liquidpixel.main.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.core.components.core.StatusComponent;
import com.liquidpixel.main.components.render.RenderComponent;
import com.liquidpixel.main.components.player.CarryComponent;
import com.liquidpixel.main.model.RenderPriority;
import com.liquidpixel.core.core.Action;
import com.liquidpixel.main.utils.Mappers;

public class CarryingSystem extends EntitySystem {
    private ComponentMapper<RenderComponent> rm = Mappers.render;
    private ComponentMapper<PositionComponent> pm = Mappers.position;
    private ComponentMapper<CarryComponent> cm = Mappers.carry;
    private ComponentMapper<StatusComponent> sm = Mappers.status;
    private ImmutableArray<Entity> carriers;

    public CarryingSystem() {
    }

    @Override
    public void addedToEngine(Engine engine) {
        carriers = engine.getEntitiesFor(Family.all(CarryComponent.class, PositionComponent.class, StatusComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity carrier : carriers) {
            CarryComponent carryComponent = cm.get(carrier);

            carryComponent.timeSincePickup += deltaTime;
            carryComponent.timeSincePutDown += deltaTime;

            if (carryComponent.isOccupied()) {
                StatusComponent statusComponent = sm.get(carrier);
                statusComponent.setAction(Action.CARRYING);
                PositionComponent positionComponent = pm.get(carrier);
                Entity item = carryComponent.getItem();

                RenderComponent renderComponent = rm.get(item);

                float x = positionComponent.getPosition().x - 0f;
                float y = positionComponent.getPosition().y + 1.5f;

                renderComponent.setPriority(RenderPriority.CARRYABLE);

                Mappers.position.get(item).setPosition(new Vector2(x, y));
            }
        }
    }
}
