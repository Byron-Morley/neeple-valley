package com.liquidpixel.main.components;

import com.badlogic.ashley.core.Component;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityComponent implements Component {


    private String id;

    @JsonCreator
    public EntityComponent() {
    }

    public EntityComponent(String id) {
        this.id = id;
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
