package com.liquidpixel.main.serialisers;

import com.badlogic.gdx.math.Vector2;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class Vector2Deserializer extends StdDeserializer<Vector2> {

    public Vector2Deserializer() {
        super(Vector2.class);
    }

    @Override
    public Vector2 deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        float x = node.get("x").floatValue();
        float y = node.get("y").floatValue();
        return new Vector2(x, y);
    }
}
