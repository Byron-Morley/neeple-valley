package com.liquidpixel.main.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FoundationComponent implements Component {

    @JsonIgnore
    ArrayList<Entity> fences;

    @JsonCreator
    public FoundationComponent() {
        this.fences = new ArrayList<>();
    }

    public void addToFences(Entity fence) {
        this.fences.add(fence);
    }

    public void clearFences() {
        this.fences.clear();
    }

    @JsonIgnore
    public ArrayList<Entity> getFences() {
        return fences;
    }
}
