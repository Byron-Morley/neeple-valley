package com.liquidpixel.main.model.ramps;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public enum Hair {
    PALE_BEIGE(0),
    LIGHT_BLONDE(1),
    HONEY_BLONDE(2);

    @JsonProperty
    private int index;

    @JsonCreator
    Hair(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}

