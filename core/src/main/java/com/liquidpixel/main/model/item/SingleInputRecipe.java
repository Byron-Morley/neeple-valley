package com.liquidpixel.main.model.item;

import com.liquidpixel.main.interfaces.IRecipe;
import com.liquidpixel.main.interfaces.IStorageItem;

import java.util.ArrayList;
import java.util.List;

public class SingleInputRecipe implements IRecipe {
    IStorageItem input;

    public SingleInputRecipe(StorageItem input) {
        this.input = input;
    }

    @Override
    public List<IStorageItem> getInput() {
        return List.of(input);
    }

    @Override
    public List<IStorageItem> getOutput() {
        return new ArrayList<>();
    }

    @Override
    public int getEffort() {
        return 0;
    }

    @Override
    public String getName() {
        return input.getName();
    }

    @Override
    public void setName(String name) {
        // do not implement
    }
}
