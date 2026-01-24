package com.liquidpixel.main.components;

import com.badlogic.ashley.core.Component;
import com.liquidpixel.item.factories.ItemFactory;
import com.liquidpixel.main.interfaces.IRecipe;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HarvestableComponent implements Component {

    String tool;

    @JsonIgnore
    IRecipe recipe;

    @JsonCreator
    public HarvestableComponent(
        @JsonProperty("recipeData") String recipeData, String tool) {
        this.tool = tool;
        this.recipe = ItemFactory.getRecipe(recipeData);
    }

    @JsonIgnore
    public IRecipe getRecipe() {
        return recipe;
    }

    @JsonProperty
    public String getRecipeData() {
        return recipe.getName();
    }

    public String getTool() {
        return tool;
    }
}
