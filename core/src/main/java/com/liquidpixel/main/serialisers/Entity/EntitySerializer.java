package com.liquidpixel.main.serialisers.Entity;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.components.EntityComponent;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class EntitySerializer extends StdSerializer<Entity> {

    public EntitySerializer() {
        super(Entity.class);
    }

    @Override
    public void serialize(Entity value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        EntityComponent component = value.getComponent(EntityComponent.class);
        if (component == null) {
            System.out.println("Entity missing EntityComponent: " + value);
        }
        gen.writeStringField("id", component.getId());
        gen.writeEndObject();
    }

}
