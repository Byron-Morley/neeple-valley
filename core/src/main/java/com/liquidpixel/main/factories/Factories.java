package com.liquidpixel.main.factories;

import com.badlogic.ashley.core.Engine;

public class Factories {
    AnimationsFactory animationsFactory;
    AgentFactory agentFactory;
    MapFactory mapFactory;

    public Factories(Engine engine) {

    }

    public AgentFactory getAgentFactory() {
        return agentFactory;
    }
}
