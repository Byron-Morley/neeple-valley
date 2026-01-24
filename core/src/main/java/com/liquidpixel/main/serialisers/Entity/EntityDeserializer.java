package com.liquidpixel.main.serialisers.Entity;

import com.badlogic.ashley.core.Entity;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

import static com.liquidpixel.main.managers.EntityIdManager.getEntityById;

public class EntityDeserializer extends StdDeserializer<Entity> {

    public EntityDeserializer() {
        super(Entity.class);
    }

    @Override
    public Entity deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        return getEntityById(node.get("id").asText());
    }
}
