package com.liquidpixel.main.serialisers;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.model.ai.Job;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public class JobDeserializer extends JsonDeserializer<Job> {

    @Override
    public Job deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        String id = node.get("id").asText();
        Entity workshop = jp.getCodec().treeToValue(node.get("workshop"), Entity.class);
        Entity agent = jp.getCodec().treeToValue(node.get("agent"), Entity.class);

        return new Job(id, workshop, agent);
    }
}
