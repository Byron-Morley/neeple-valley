package com.liquidpixel.main.systems.spectral;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.core.components.core.StatusComponent;
import com.liquidpixel.main.components.camera.CameraComponent;
import com.liquidpixel.main.components.player.PlayerControlComponent;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.managers.core.PlayerInputManager;
import com.liquidpixel.main.model.player.PlayerAction;
import com.liquidpixel.core.core.Action;
import com.liquidpixel.core.core.Direction;

import java.util.LinkedList;
import java.util.Stack;

public class SpectralInputSystem extends IteratingSystem {
    PlayerInputManager playerInputManager;
    float minimumZoom = 0.01f;
    float maximumZoom = 0.1f;
    float zoomAmount = 0.01f;

    public SpectralInputSystem(PlayerInputManager playerInputManager) {
        super(Family.all(PlayerControlComponent.class, PositionComponent.class, CameraComponent.class).get());
        this.playerInputManager = playerInputManager;
    }

    @Override
    protected void processEntity(Entity player, float deltaTime) {

        Stack<Direction> movementKeysPressed = playerInputManager.getMovementKeysPressed();
        Stack<PlayerAction> actionKeysPressed = playerInputManager.getActionKeyPressed();

        processMovement(movementKeysPressed, actionKeysPressed, player);
        processZoom();
    }

    private void processZoom() {
        LinkedList<Float> scrollQueue = playerInputManager.getScrollQueue();

        if (scrollQueue.size() > 0) {
            float scroll = scrollQueue.poll();

            OrthographicCamera camera = GameResources.get().getCamera();

            if (scroll > 0 && camera.zoom < maximumZoom) {
                camera.zoom += zoomAmount;
            } else if (scroll < 0 && camera.zoom > minimumZoom) {
                camera.zoom -= zoomAmount;
            }

            camera.update();
            camera.zoom = MathUtils.clamp(camera.zoom, minimumZoom, maximumZoom);
        }
    }

    private void processMovement(Stack<Direction> movementKeysPressed, Stack<PlayerAction> actionKeysPressed, Entity player) {
        StatusComponent status = player.getComponent(StatusComponent.class);
        if (movementKeysPressed.isEmpty() && actionKeysPressed.isEmpty()) {
            status.setAction(Action.STANDING);
        } else {
            if (!status.getAction().equals(Action.CHOP)) {
                status.setAction(Action.WALKING);
            }
        }

        float x = 0;
        float y = 0;

        if (movementKeysPressed.contains(Direction.UP)) {
            y += 1;
        }
        if (movementKeysPressed.contains(Direction.DOWN)) {
            y -= 1;
        }
        if (movementKeysPressed.contains(Direction.LEFT)) {
            x -= 1;
        }
        if (movementKeysPressed.contains(Direction.RIGHT)) {
            x += 1;
        }

        if (x == 1) status.setDirection(Direction.RIGHT);
        if (x == -1) status.setDirection(Direction.LEFT);
        if (y == 1) status.setDirection(Direction.UP);
        if (y == -1) status.setDirection(Direction.DOWN);

        //TODO fix the animation being null
//        if (x == 1 && y == 1) status.setDirection(Direction.UP_RIGHT);
//        if (x == 1 && y == -1) status.setDirection(Direction.DOWN_RIGHT);
//        if (x == -1 && y == 1) status.setDirection(Direction.UP_LEFT);
//        if (x == -1 && y == -1) status.setDirection(Direction.DOWN_LEFT);

//        Vector2 newPosition = getNewPosition(deltaTime, movementKeysPressed);
//        cameraComponent.setPosition(cameraComponent.getPosition().add(newPosition));
    }
}
