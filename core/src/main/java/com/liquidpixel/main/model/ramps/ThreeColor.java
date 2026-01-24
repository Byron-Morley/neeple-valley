package com.liquidpixel.main.model.ramps;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public enum ThreeColor {
    DARK_GREEN(0),
    GRAY(1),
    SLATE_GRAY(2),
    STEEL_BLUE(3),
    DULL_PURPLE(4),
    PALE_BLUE(5),
    NAVY_BLUE(6),
    LIGHT_SLATE_BLUE(7),
    SEAFOAM(8),
    AQUA_BLUE(9),
    CYAN(10),
    DULL_TEAL(11),
    JADE_GREEN(12),
    ROYAL_BLUE(13),
    DULL_BLUE(14),
    BRIGHT_GREEN(15),
    FOREST_GREEN(16),
    DARK_TEAL(17),
    LIGHT_CYAN(18),
    MINT_GREEN(19),
    LIME_GREEN(20),
    YELLOW_GREEN(21),
    MUSTARD(22),
    GOLDEN_YELLOW(23),
    AMBER(24),
    BURNT_ORANGE(25),
    RED_ORANGE(26),
    BRIGHT_RED(27),
    CRIMSON(28),
    HOT_PINK(29),
    MAGENTA_PURPLE(30),
    VIOLET(31),
    INDIGO(32),
    SLATE_PURPLE(33),
    DARK_SLATE(34);


    @JsonProperty
    private int index;

    @JsonCreator
    ThreeColor(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
    }

