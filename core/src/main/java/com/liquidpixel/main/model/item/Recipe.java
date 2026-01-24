package com.liquidpixel.main.model.item;

import com.liquidpixel.main.interfaces.IRecipe;
import com.liquidpixel.main.interfaces.IStorageItem;

import java.util.ArrayList;
import java.util.List;

public class Recipe implements IRecipe {

    String name;
    List<IStorageItem> input;
    List<IStorageItem> output;
    int effort;

    public Recipe() {
        input = new ArrayList<>();
        output = new ArrayList<>();
    }

    public Recipe(String name, List<IStorageItem> input, List<IStorageItem> output, int effort) {
        this.name = name;
        this.input = input;
        this.output = output;
        this.effort = effort;
    }

    public List<IStorageItem> getInput() {
        return input;
    }

    public List<IStorageItem> getOutput() {
        return output;
    }

    public int getEffort() {
        return effort;
    }

    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
