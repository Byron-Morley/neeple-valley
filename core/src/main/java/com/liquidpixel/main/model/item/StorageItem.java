package com.liquidpixel.main.model.item;

import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.sprite.model.GameSprite;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StorageItem implements IStorageItem {

    int quantity;

    String name;

    int stackSize;

    GameSprite sprite;

    public StorageItem(String name, int quantity, int stackSize, GameSprite sprite) {
        this.name = name;
        this.quantity = quantity;
        this.stackSize = stackSize;
        this.sprite = sprite;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public int getStackSize() {
        return stackSize;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSpriteName() {
        return sprite.getSpriteName();
    }

    @Override
    public GameSprite getSprite() {
        return sprite;
    }

    @Override
    public void setSprite(GameSprite sprite) {
        this.sprite = sprite;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
