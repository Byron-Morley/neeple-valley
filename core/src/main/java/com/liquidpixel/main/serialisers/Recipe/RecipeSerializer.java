package com.liquidpixel.main.serialisers.Recipe;

import com.liquidpixel.main.model.item.Recipe;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class RecipeSerializer extends StdSerializer<Recipe> {

    public RecipeSerializer() {
        super(Recipe.class);
    }

    @Override
    public void serializeWithType(Recipe iRecipe, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        typeSer.writeTypePrefixForObject(iRecipe, gen);
        serialize(iRecipe, gen, serializers);
        typeSer.writeTypeSuffixForObject(iRecipe, gen);
    }

    @Override
    public void serialize(Recipe iRecipe, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeStringField("@type", Recipe.class.getName());
        gen.writeStringField("name", iRecipe.getName());
    }
}
