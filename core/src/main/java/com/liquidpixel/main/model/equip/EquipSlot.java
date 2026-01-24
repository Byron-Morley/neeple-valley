package com.liquidpixel.main.model.equip;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public enum EquipSlot {
    SHADOW(4, "3color"),
    BODY(5, "skin"),
    HAIR_BASE(6, "hair"),
    EYES(8, "3color"),
    HEAD(9, "none"),
    BELT(11, "3color"),
    UNDER(11, "3color"),
    CHEST(12, "3color"),
    OUTER(12, "3color"),
    PANTS(13, "3color"),
    GLOVES(13, "3color"),
    SOCKS(13, "3color"),
    SHOES(14, "3color"),
    WEAPON(15, "3color"),
    SHIELD(16, "3color"),
    BOW(17, "3color"),
    RING(18, "3color"),
    NECK(19, "3color"),
    HANDS(20, "3color");

    @JsonProperty
    private int renderPriority;

    @JsonProperty
    private String ramp;

    @JsonCreator
    EquipSlot(int renderPriority, String ramp) {
        this.renderPriority = renderPriority;
        this.ramp = ramp;
    }

    public String getRamp() {
        return ramp;
    }

    public int getRenderPriority() {
        return renderPriority;
    }
}

