package com.liquidpixel.main.model.ai;

import com.badlogic.ashley.core.Entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Job {

    @JsonProperty
    String id;

    @JsonProperty
    Entity workshop;

    @JsonProperty
    Entity agent;


    @JsonCreator
    public Job(String id, Entity workshop, Entity agent) {
        this.workshop = workshop;
        this.agent = agent;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Entity getWorkshop() {
        return workshop;
    }

    public Entity getAgent() {
        return agent;
    }
}
