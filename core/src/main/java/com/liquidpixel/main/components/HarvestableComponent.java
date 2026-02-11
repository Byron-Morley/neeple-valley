package com.liquidpixel.main.components;

import com.badlogic.ashley.core.Component;
import com.liquidpixel.item.factories.ItemFactory;
import com.liquidpixel.main.interfaces.IRecipe;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HarvestableComponent implements Component {

    String tool;

    IRecipe recipe;

    public HarvestableComponent(
        @JsonProperty("recipeData") String recipeData, String tool) {
        this.tool = tool;
        this.recipe = ItemFactory.getRecipe(recipeData);
    }

    public IRecipe getRecipe() {
        return recipe;
    }

    public String getRecipeData() {
        return recipe.getName();
    }

    public String getTool() {
        return tool;
    }
}
