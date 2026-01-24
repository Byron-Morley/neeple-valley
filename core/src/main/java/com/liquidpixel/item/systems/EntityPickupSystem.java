package com.liquidpixel.item.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.item.components.SpectralPickupComponent;
import com.liquidpixel.main.interfaces.services.ICameraService;
import com.liquidpixel.main.utils.Mappers;

public class EntityPickupSystem extends IteratingSystem {

    ICameraService cameraService;

    public EntityPickupSystem(ICameraService cameraService) {
        super(Family.all(SpectralPickupComponent.class, PositionComponent.class).get());
        this.cameraService = cameraService;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Vector2 clickedPosition = cameraService.getUnprojectedCursorPosition();
        PositionComponent positionComponent = Mappers.position.get(entity);
        Vector2 newPosition = new Vector2((float) Math.floor(clickedPosition.x), (float) Math.floor(clickedPosition.y));
        positionComponent.setPosition(newPosition);
    }
}
