package com.liquidpixel.main.model.item;

public enum ClickBehaviorType {
    SETTLEMENT_DETAILS("settlement_details"),
    TRADE_WINDOW("trade_window"),
    STORAGE_INFO("storage_window"),
    HARVEST("harvest"),
    AREA("area"),
    PAINT("paint"),
    SPAWN("spawn"),
    DESTROY("destroy"),
    FARM_WINDOW("farm_window"),
    UNIVERSAL_WINDOW("window"),
    RESOURCE_INFO("resource_info");

    private final String id;

    ClickBehaviorType(String id) {
        this.id = id;
    }

    public static ClickBehaviorType fromString(String id) {
        for (ClickBehaviorType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }
}
