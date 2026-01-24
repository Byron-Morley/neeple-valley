package com.liquidpixel.main.model.equip;

public enum EquipType {
    SPEAR("SPEAR"),
    DAGGER("DAGGER"),
    SWORD("SWORD"),
    BOW("BOW"),
    ROD("ROD"),
    WAND("WAND"),
    AXE("AXE"),
    PICKAXE("PICKAXE");

    private String name;

    EquipType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
