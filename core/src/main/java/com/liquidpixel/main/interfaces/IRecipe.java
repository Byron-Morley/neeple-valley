package com.liquidpixel.main.interfaces;

import com.liquidpixel.main.interfaces.IStorageItem;

import java.util.List;

public interface IRecipe {

    List<IStorageItem> getInput();

    List<IStorageItem> getOutput();

    int getEffort();

    String getName();

    void setName(String name);
}
