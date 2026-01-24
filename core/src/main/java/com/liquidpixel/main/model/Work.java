package com.liquidpixel.main.model;

public enum Work {
    CONSTRUCT(1),
    TRANSPORT(2),
    HARVEST(3),
    REPAIR(4),
    DECONSTRUCT(5),
    UPGRADE(6),
    DESTROY(7),
    RESEARCH(8),
    CRAFT(9),
    NOT_SET(100);

    private int value;

    Work(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
