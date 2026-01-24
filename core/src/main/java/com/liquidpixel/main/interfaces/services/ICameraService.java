package com.liquidpixel.main.interfaces.services;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.generators.map.MapConfig;
import com.liquidpixel.main.interfaces.services.IAgentService;

public interface ICameraService {
    Vector2 getCursorLocation();
    GridPoint2 getCursorGridLocation();
    Vector2 getUnprojectedCursorPosition();
    void project(Vector2 position);
    void setCameraPosition(float x, float y);
    void resetCamera(IAgentService agentService, MapConfig mapConfig);
}
