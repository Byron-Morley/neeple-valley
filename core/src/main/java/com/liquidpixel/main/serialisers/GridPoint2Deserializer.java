package com.liquidpixel.main.serialisers;

import com.badlogic.gdx.math.GridPoint2;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class GridPoint2Deserializer extends StdDeserializer<GridPoint2> {

    public GridPoint2Deserializer() {
        super(GridPoint2.class);
    }

    @Override
    public GridPoint2 deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        int x = node.get("x").asInt();
        int y = node.get("y").asInt();
        return new GridPoint2(x, y);
    }
}
