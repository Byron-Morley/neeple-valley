package com.liquidpixel.main.model;

public enum RenderPriority {
    UI(1),
    FOREGROUND(2),
    CARRYABLE(3),
    AGENT(4),
    ITEM(4),
    CROP(5),           // Crops render above buildings but below items
    SCENERY(6),
    BUILDING(7),       // Buildings render below most items
    FLOOR_RESOURCE(8),
    FLOOR_UI(9),
    BACKGROUND(10);

    private int value;

    RenderPriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
