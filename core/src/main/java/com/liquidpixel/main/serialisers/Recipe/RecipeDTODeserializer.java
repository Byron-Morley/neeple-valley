package com.liquidpixel.main.serialisers.Recipe;

import com.liquidpixel.main.model.item.RecipeDTO;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RecipeDTODeserializer extends JsonDeserializer<HashMap<String, RecipeDTO>> {
    @Override
    public HashMap<String, RecipeDTO> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        HashMap<String, RecipeDTO> recipes = new HashMap<>();
        JsonNode node = jp.getCodec().readTree(jp);

        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String recipeName = entry.getKey();
            RecipeDTO recipe = jp.getCodec().treeToValue(entry.getValue(), RecipeDTO.class);
            recipe.setName(recipeName);
            recipes.put(recipeName, recipe);
        }

        return recipes;
    }
}
