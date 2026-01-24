package com.liquidpixel.main.serialisers;


import com.badlogic.gdx.math.Vector2;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class Vector2Serializer extends StdSerializer<Vector2> {

    public Vector2Serializer() {
        super(Vector2.class);
    }

    @Override
    public void serialize(Vector2 value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("x", value.x);
        gen.writeNumberField("y", value.y);
        gen.writeEndObject();
    }
}
