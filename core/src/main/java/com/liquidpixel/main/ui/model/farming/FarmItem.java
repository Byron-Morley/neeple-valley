package com.liquidpixel.main.ui.model.farming;

import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.model.item.StorageItem;

public class FarmItem extends StorageItem {

    int days;
    int hours;
    boolean isPlanted;
    float growthProgress; // 0.0 to 1.0

    public FarmItem(IStorageItem storageItem, int days, int hours) {
        super(storageItem.getName(), storageItem.getQuantity(), storageItem.getStackSize(), storageItem.getSprite());
        this.days = days;
        this.hours = hours;
        this.isPlanted = false;
        this.growthProgress = 0.5f;
    }

    public FarmItem(IStorageItem storageItem, int days, int hours, boolean isPlanted, float growthProgress) {
        super(storageItem.getName(), storageItem.getQuantity(), storageItem.getStackSize(), storageItem.getSprite());
        this.days = days;
        this.hours = hours;
        this.isPlanted = isPlanted;
        this.growthProgress = growthProgress;
    }

    public int getDays() {
        return days;
    }

    public int getHours() {
        return hours;
    }

    public boolean isPlanted() {
        return isPlanted;
    }

    public void setPlanted(boolean planted) {
        isPlanted = planted;
    }

    public float getGrowthProgress() {
        return growthProgress;
    }

    public void setGrowthProgress(float growthProgress) {
        this.growthProgress = growthProgress;
    }
}
