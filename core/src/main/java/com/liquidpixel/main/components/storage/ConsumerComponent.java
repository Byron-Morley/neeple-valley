package com.liquidpixel.main.components.storage;

import com.badlogic.ashley.core.Component;
import com.liquidpixel.main.interfaces.IRecipe;

public class ConsumerComponent implements Component {

    IRecipe recipe;

    public ConsumerComponent(IRecipe recipe) {
        this.recipe = recipe;
    }

    public IRecipe getRecipe() {
        return recipe;
    }
}
