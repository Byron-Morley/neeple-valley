package com.liquidpixel.main.serialisers;

import com.liquidpixel.main.model.sprite.NodeType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class NodeTypeDeserializer extends StdDeserializer<NodeType> {

    public NodeTypeDeserializer() {
        super(NodeType.class);
    }

    @Override
    public NodeType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        int n = node.get("n").asInt();
        System.out.println("n "+n);
        return NodeType.fromValue(n);
    }
}
