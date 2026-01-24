package com.liquidpixel.main.serialisers;

import com.badlogic.gdx.math.GridPoint2;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class GridPoint2Serializer extends StdSerializer<GridPoint2> {

    public GridPoint2Serializer() {
        super(GridPoint2.class);
    }

    @Override
    public void serialize(GridPoint2 value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("x", value.x);
        gen.writeNumberField("y", value.y);
        gen.writeEndObject();
    }
}
