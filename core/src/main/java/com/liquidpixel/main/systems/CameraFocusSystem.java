package com.liquidpixel.main.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.camera.CameraFocusComponent;
import com.liquidpixel.main.interfaces.services.ICameraService;

import com.liquidpixel.main.utils.Mappers;
public class CameraFocusSystem extends IteratingSystem {
    ICameraService cameraService;

    public CameraFocusSystem(ICameraService cameraService) {
        super(Family.all(CameraFocusComponent.class, PositionComponent.class).get());
        this.cameraService = cameraService;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = Mappers.position.get(entity);
        cameraService.setCameraPosition(position.getX(), position.getY());
    }
}
