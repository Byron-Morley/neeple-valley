package com.liquidpixel.main.utils;

public enum Biome {
    WATER("water"),
    DESERT("desert"),
    LIGHT_GRASS("light-grass"),
    DARK_GRASS("dark-grass"),
    LIGHT_DIRT("light-dirt"),
    MEDIUM_DIRT("medium-dirt"),
    DARK_DIRT("dark-dirt"),
    BURNT_DIRT("burnt-dirt");
    private String name;

    Biome(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Biome fromString(String name) {
        if (name != null) {
            for (Biome a : Biome.values()) {
                if (name.startsWith(a.name)) {
                    return a;
                }
            }
        }
        return null;
    }
}
