package com.liquidpixel.main.serialisers;

import com.badlogic.gdx.math.GridPoint2;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import java.io.IOException;

public class GridPoint2KeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) {
        // Remove parentheses and split coordinates
        key = key.replace("(", "").replace(")", "");
        String[] coords = key.split(",");
        return new GridPoint2(
            Integer.parseInt(coords[0].trim()),
            Integer.parseInt(coords[1].trim())
        );
    }
}
