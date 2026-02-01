package com.liquidpixel.main.model.item;

public class ResourceInformation {

    int yield; //amount of resource per harvest
    float harvestTime = 1f; // in days
    int stackSize = 100;
    float foodUnit = 0f;

    public ResourceInformation() {
    }

    public int getYield() {
        return yield;
    }

    public float getHarvestTime() {
        return harvestTime;
    }

    public int getStackSize() {
        return stackSize;
    }

    public float getFoodUnit() {
        return foodUnit;
    }
}
