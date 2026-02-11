package com.liquidpixel.main.components.agent;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class AgentJobComponent  implements Component {

    Entity job;

    public AgentJobComponent(Entity job) {
        this.job = job;
    }

    public Entity getJob() {
        return job;
    }

    public void setJob(Entity job) {
        this.job = job;
    }
}
