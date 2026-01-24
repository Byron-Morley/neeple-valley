package com.liquidpixel.main.interfaces.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.liquidpixel.main.generators.map.MapConfig;
import com.liquidpixel.main.interfaces.services.IAgentService;

public interface ICameraManager {
    OrthographicCamera getCamera();
    Viewport getViewport();
    void setPosition(float x, float y);
    void render(float delta, SpriteBatch batch);
    void resize(int width, int height);
    void resetCamera(IAgentService agentService, MapConfig mapConfig);
}
