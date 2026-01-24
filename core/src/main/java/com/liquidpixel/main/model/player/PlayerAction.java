package com.liquidpixel.main.model.player;
public enum PlayerAction {
    ACTION("ACTION");
    private String name;

    PlayerAction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static PlayerAction fromString(String name) {
        if (name != null) {
            for (PlayerAction a : PlayerAction.values()) {
                if (name.startsWith(a.name)) {
                    return a;
                }
            }
        }
        return null;
    }
}
