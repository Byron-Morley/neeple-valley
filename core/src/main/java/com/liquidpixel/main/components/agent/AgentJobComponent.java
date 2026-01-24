package com.liquidpixel.main.components.agent;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.model.ai.Job;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
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
