package com.liquidpixel.main.systems.spectral;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.core.components.core.StatusComponent;
import com.liquidpixel.main.components.BodyComponent;
import com.liquidpixel.main.components.VelocityComponent;
import com.liquidpixel.main.components.camera.CameraComponent;
import com.liquidpixel.main.components.player.PlayerControlComponent;
import com.liquidpixel.core.core.Action;
import com.liquidpixel.main.utils.Mappers;

public class SpectralMovementSystem extends IteratingSystem {
    private ComponentMapper<VelocityComponent> vm = Mappers.velocity;
    private ComponentMapper<StatusComponent> sm = Mappers.status;
    private ComponentMapper<BodyComponent> bm = Mappers.body;

    public SpectralMovementSystem() {
        super(Family.all(
            StatusComponent.class,
            PlayerControlComponent.class,
            PositionComponent.class,
            VelocityComponent.class,
            CameraComponent.class
        ).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        VelocityComponent velocity = vm.get(entity);
        StatusComponent status = sm.get(entity);
        PositionComponent position = Mappers.position.get(entity);

        int toggleX = 0;
        int toggleY = 0;

        if (Action.WALKING.equals(status.getAction())) {
            switch (status.getDirection()) {
                case UP:
                    toggleY = 1;
                    break;
                case DOWN:
                    toggleY = -1;
                    break;
                case LEFT:
                    toggleX = -1;
                    break;
                case RIGHT:
                    toggleX = 1;
                    break;
                case UP_LEFT:
                    toggleX = -1;
                    toggleY = 1;
                    break;
                case UP_RIGHT:
                    toggleX = 1;
                    toggleY = 1;
                    break;
                case DOWN_LEFT:
                    toggleX = -1;
                    toggleY = -1;
                    break;
                case DOWN_RIGHT:
                    toggleX = 1;
                    toggleY = -1;
                    break;
            }
        }

        float speed = velocity.getVelocity();

        if (toggleX != 0 && toggleY != 0) {
            speed = speed / (float) Math.sqrt(2);
        }
        float x = speed * toggleX * deltaTime;
        float y = speed * toggleY * deltaTime;

        Vector2 newPosition = position.getPosition().add(new Vector2(x, y));
        position.setPosition(newPosition);
    }
}
