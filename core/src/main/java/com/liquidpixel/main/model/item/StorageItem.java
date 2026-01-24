package com.liquidpixel.main.model.item;

import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.sprite.model.GameSprite;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StorageItem implements IStorageItem {

    @JsonProperty
    int quantity;

    @JsonProperty
    String name;

    @JsonIgnore
    int stackSize;

    @JsonIgnore
    GameSprite sprite;

    //modify this for deserialization later, make custom deserializer
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

    @JsonIgnore
    @Override
    public int getStackSize() {
        return stackSize;
    }

    @JsonProperty("name")
    @Override
    public String getName() {
        return name;
    }

    @JsonIgnore
    @Override
    public String getSpriteName() {
        return sprite.getSpriteName();
    }

    @Override
    public GameSprite getSprite() {
        return sprite;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
