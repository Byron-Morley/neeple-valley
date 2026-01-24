package com.liquidpixel.main.serialisers;

import com.liquidpixel.main.components.ClickableComponent;
import com.liquidpixel.selection.api.IClickBehaviorService;
import com.liquidpixel.main.interfaces.ui.IWindowService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class ClickableComponentDeserializer extends StdDeserializer<ClickableComponent> {

    IWindowService windowService;
    IClickBehaviorService clickBehaviorService;

    public ClickableComponentDeserializer(IWindowService windowService, IClickBehaviorService clickBehaviorService) {
        super(ClickableComponent.class);
        this.clickBehaviorService = clickBehaviorService;
        this.windowService = windowService;
    }

    @Override
    public ClickableComponent deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String behaviorId = node.get("behaviorId").asText();
        if (behaviorId == null) {
            return null;
        }
        return new ClickableComponent(clickBehaviorService.createClickBehavior(behaviorId), behaviorId);
    }
}
