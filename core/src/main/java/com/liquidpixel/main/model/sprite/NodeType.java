package com.liquidpixel.main.model.sprite;

public enum NodeType {
    TILE_EMPTY(0),
    TILE_FLOOR(1),
    TILE_WALL(2);

    private int value;

    NodeType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static NodeType fromValue(int value) {
        for (NodeType type : NodeType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return TILE_EMPTY;
    }
}
