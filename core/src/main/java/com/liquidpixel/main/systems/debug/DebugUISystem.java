package com.liquidpixel.main.systems.debug;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.core.components.core.StatusComponent;
import com.liquidpixel.main.components.*;
import com.liquidpixel.main.components.player.PlayerControlComponent;
import com.liquidpixel.main.ui.controller.OverlayUIController;
import com.liquidpixel.main.utils.Mappers;

public class DebugUISystem extends IteratingSystem {
    private ComponentMapper<VelocityComponent> vm = Mappers.velocity;
    private ComponentMapper<StatusComponent> sm = Mappers.status;
    private ComponentMapper<BodyComponent> bm = Mappers.body;
    private ComponentMapper<PositionComponent> pm = Mappers.position;
    private OverlayUIController overlayController;

    public DebugUISystem(OverlayUIController overlayUIController) {
        super(Family.all(PlayerControlComponent.class).get());
        this.overlayController = overlayUIController;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}
