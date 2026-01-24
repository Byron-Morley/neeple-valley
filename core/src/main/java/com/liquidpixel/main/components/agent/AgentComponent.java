package com.liquidpixel.main.components.agent;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentComponent implements Component {

    @JsonProperty
    String id;

    Entity equipped;


    @JsonCreator
    public AgentComponent(
        @JsonProperty("id") String id
    ) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Entity getEquipped() {
        return equipped;
    }

    public void setEquipped(Entity equipped) {
        this.equipped = equipped;
    }
}
