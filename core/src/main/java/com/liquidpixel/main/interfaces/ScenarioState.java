package com.liquidpixel.main.interfaces;

public class ScenarioState {
    private final String id;
    private final String displayName;

    public ScenarioState(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }


    @Override
    public String toString() {
        return displayName;
    }
}
