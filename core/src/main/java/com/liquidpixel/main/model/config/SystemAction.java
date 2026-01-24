package com.liquidpixel.main.model.config;

public enum SystemAction {
    TOGGLE_CONNECTIONS("TOGGLE_CONNECTIONS"),
    TOGGLE_PAUSE("TOGGLE_PAUSE"),
    SPEED_NORMAL("SPEED_NORMAL"),
    SPEED_FAST("SPEED_FAST"),
    SPEED_FASTER("SPEED_FASTER"),
    TOGGLE_WORK_ORDERS("TOGGLE_WORK_ORDERS"),
    TOGGLE_PEOPLE_UI("TOGGLE_PEOPLE_UI");

    private String name;

    SystemAction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SystemAction fromString(String name) {
        if (name != null) {
            for (SystemAction action : SystemAction.values()) {
                if (name.equals(action.name)) {
                    return action;
                }
            }
        }
        return null;
    }
}
