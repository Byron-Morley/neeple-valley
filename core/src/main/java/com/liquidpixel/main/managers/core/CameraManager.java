package com.liquidpixel.main.managers.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.*;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.generators.map.MapConfig;
import com.liquidpixel.main.interfaces.managers.ICameraManager;
import com.liquidpixel.main.interfaces.services.IAgentService;
import com.liquidpixel.main.interfaces.services.ICameraService;
import com.liquidpixel.main.services.CameraService;

public class CameraManager implements ICameraManager {
    static final float MAXIMUM_DISTANCE_FROM_SHAKE = 20;
    static final int Z = 0;

    private Entity cameraEntity;

    ScreenShakeManager shakeManager;
    OrthographicCamera camera;
    Vector3 position;
    ExtendViewport viewport;
    ICameraService cameraService;

    public CameraManager() {
        cameraService = new CameraService(this);
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        this.position = new Vector3(0, 0, Z);
        this.shakeManager = new ScreenShakeManager();
        this.camera = GameResources.get().getCamera();
        camera.zoom = 0.01f;
        viewport = new ExtendViewport(width, height, this.camera);
        viewport.setScaling(Scaling.none);
    }

    public void shake(float power, float duration, Vector2 epicenter) {
        if (cameraCloseToEpicenter(epicenter))
            shakeManager.shake(power, duration);
    }

    private boolean cameraCloseToEpicenter(Vector2 epicenter) {
        return new Vector3(epicenter, Z).sub(position).len() < MAXIMUM_DISTANCE_FROM_SHAKE;
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        viewport.apply();
    }

    public void render(float delta, SpriteBatch batch) {
        camera.position.set(position);

        if (shakeManager.getShakingTimeLeft() > 0)
            camera.translate(shakeManager.tick(delta));

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
    }

    public void setPosition(float x, float y) {
        this.position = new Vector3(x, y, Z);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public ICameraService getCameraService() {
        return cameraService;
    }

    public ExtendViewport getViewport() {
        return viewport;
    }

    public void resetCamera(IAgentService agentService, MapConfig mapConfig) {
        cameraEntity = agentService.spawnAgent(new GridPoint2(mapConfig.getCameraStartLocationX(), mapConfig.getCameraStartLocationY()), "camera");
    }
}
