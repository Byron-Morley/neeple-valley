package com.liquidpixel.main.model.item;

import java.util.List;
import java.util.ArrayList;


public class RecipeDTO {

    String name;
    List<StorageItemDTO> input;
    List<StorageItemDTO> output;
    int effort;

    public RecipeDTO() {
        input = new ArrayList<StorageItemDTO>();
        output = new ArrayList<StorageItemDTO>();
    }

    public List<StorageItemDTO> getInput() {
        return input;
    }

    public List<StorageItemDTO> getOutput() {
        return output;
    }

    public int getEffort() {
        return effort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
