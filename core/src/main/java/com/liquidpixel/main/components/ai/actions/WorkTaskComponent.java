package com.liquidpixel.main.components.ai.actions;

import com.badlogic.ashley.core.Component;
import com.liquidpixel.main.interfaces.IRecipe;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WorkTaskComponent implements Component {
    public enum State {
        WORKING,
        COMPLETED,
        FAILED
    }

    @JsonProperty
    public State state = State.WORKING;

    @JsonProperty
    IRecipe recipe;

    @JsonProperty
    float progress;

    public WorkTaskComponent(IRecipe recipe) {
        this.recipe = recipe;
        this.progress = 0;
    }

    public IRecipe getRecipe() {
        return recipe;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }
}
