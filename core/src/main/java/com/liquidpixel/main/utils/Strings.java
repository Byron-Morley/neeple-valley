package com.liquidpixel.main.utils;

public class Strings {
    public static String getStringBeforeUnderscore(String input) {
        if (input == null) {
            return null;
        }
        int underscoreIndex = input.indexOf('_');
        return underscoreIndex != -1 ? input.substring(0, underscoreIndex) : input;
    }
}
