package com.liquidpixel.main.model.sprite;

import com.liquidpixel.core.core.Status;

import java.util.Map;

public class RawAnimationModel {
    private String name;
    private float frameDuration;
    private Map<Status, SubAnimationModel> subAnimations;

    public RawAnimationModel() {
    }

    public String getName() {
        return name;
    }

    public float getFrameDuration() {
        return frameDuration;
    }

    public Map<Status, SubAnimationModel> getSubAnimations() {
        return subAnimations;
    }

    @Override
    public String toString() {
        return "RawAnimationModel{" +
            "name='" + name + '\'' +
            ", frameDuration=" + frameDuration +
            ", subAnimations=" + subAnimations +
            '}';
    }
}
