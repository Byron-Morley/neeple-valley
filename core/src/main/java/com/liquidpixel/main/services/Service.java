package com.liquidpixel.main.services;

import com.badlogic.ashley.core.Engine;
import com.liquidpixel.main.engine.GameResources;

public abstract class Service {
    private final Engine engine;

    public Service(Engine engine) {
        this.engine = engine;
    }

    public Service() {
        this.engine = GameResources.get().getEngine();
    }

    protected Engine getEngine() {
        return engine;
    }
}
