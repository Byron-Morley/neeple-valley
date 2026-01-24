package com.liquidpixel.main.serialisers.Recipe;


import com.liquidpixel.main.model.item.Recipe;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

import static com.liquidpixel.item.factories.ItemFactory.getRecipe;

public class RecipeDeserializer extends JsonDeserializer<Recipe> {

    public RecipeDeserializer() {
        super();
    }

    @Override
    public Recipe deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
       JsonNode node = parser.getCodec().readTree(parser);
       return (Recipe) getRecipe(node.get("name").asText());
    }
}
