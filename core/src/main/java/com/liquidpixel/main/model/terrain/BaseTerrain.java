package com.liquidpixel.main.model.terrain;

public enum BaseTerrain {
    SPRING("spring-default/terrain"),
    SUMMER("summer-default/terrain"),
    AUTUMN("autumn-default/terrain"),
    WINTER("winter-default/terrain");

    private final String id;

    BaseTerrain(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static BaseTerrain fromString(String id) {
        for (BaseTerrain type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }
}
