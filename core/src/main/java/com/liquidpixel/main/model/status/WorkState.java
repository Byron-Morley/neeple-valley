package com.liquidpixel.main.model.status;

public enum WorkState {
    IDLE("IDLE"),
    FAILED("FAILED"),
    COMPLETED("COMPLETED"),
    MOVING("MOVING"),
    MOVING_TO_LOCATION("MOVING_TO_LOCATION"),
    MOVING_TO_DESTINATION("MOVING_TO_DESTINATION"),
    PICKING_UP("PICKING_UP"),
    DROPPING_OFF("DROPPING_OFF"),
    MOVING_TO_ITEM("MOVING_TO_ITEM"),
    HARVEST("HARVEST"),
    HARVESTING("HARVESTING");

    private String name;

    WorkState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static WorkState fromString(String name) {
        if (name != null) {
            for (WorkState a : WorkState.values()) {
                if (name.startsWith(a.name)) {
                    return a;
                }
            }
        }
        return null;
    }
}
