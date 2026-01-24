package com.liquidpixel.main.components.items;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InStorageComponent implements Component {

    @JsonProperty
    Entity storage;

    public InStorageComponent() {
    }

    public InStorageComponent(Entity storage) {
        this.storage = storage;
    }

    public Entity getStorage() {
        return storage;
    }
}
