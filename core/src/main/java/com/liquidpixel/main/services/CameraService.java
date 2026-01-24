package com.liquidpixel.main.services;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.liquidpixel.main.generators.map.MapConfig;
import com.liquidpixel.main.interfaces.managers.ICameraManager;
import com.liquidpixel.main.interfaces.services.IAgentService;
import com.liquidpixel.main.interfaces.services.ICameraService;

public class CameraService extends Service implements ICameraService {
    ICameraManager cameraManager;
    Entity cameraEntity;

    public CameraService(ICameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @Override
    public Vector2 getCursorLocation() {
        Vector3 cursor = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f);
        cameraManager.getCamera().unproject(cursor);
        return new Vector2((int) Math.floor(cursor.x), (int) Math.floor(cursor.y));
    }

    @Override
    public GridPoint2 getCursorGridLocation() {
        Vector3 cursor = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f);
        cameraManager.getCamera().unproject(cursor);

        return new GridPoint2((int) Math.floor(cursor.x), (int) Math.floor(cursor.y));
    }

    @Override
    public Vector2 getUnprojectedCursorPosition() {
        Vector3 cursor = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f);
        cameraManager.getCamera().unproject(cursor);

        return new Vector2(cursor.x, cursor.y);
    }

    @Override
    public void project(Vector2 position) {
        cameraManager.getViewport().project(position);
    }

    @Override
    public void setCameraPosition(float x, float y) {
        cameraManager.setPosition(x, y);
    }

    public void resetCamera(IAgentService agentService, MapConfig mapConfig) {
        cameraManager.resetCamera(agentService, mapConfig);
    }
}
