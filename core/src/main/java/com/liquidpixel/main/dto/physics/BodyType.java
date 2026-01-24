package com.liquidpixel.main.dto.physics;

public enum BodyType {

    STATIC("STATIC"),
    DYNAMIC("DYNAMIC");
    private String type;

    BodyType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
