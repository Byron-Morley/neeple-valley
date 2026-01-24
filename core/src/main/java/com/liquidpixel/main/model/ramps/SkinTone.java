package com.liquidpixel.main.model.ramps;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public enum SkinTone {
    IVORY(0),
    LIGHT_BEIGE(1),
    PALE_TAN(2),
    WARM_TAN(3),
    HONEY_BEIGE(4),
    GOLDEN_TAN(5),
    CARAMEL(6),
    COPPER_BROWN(7),
    WALNUT_BROWN(8),
    MOCHA(9),
    ESPRESSO(10),
    DEEP_EBONY(11);


    @JsonProperty
    private int index;

    @JsonCreator
    SkinTone(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}

