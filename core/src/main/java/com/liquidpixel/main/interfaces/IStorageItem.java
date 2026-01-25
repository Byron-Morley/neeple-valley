package com.liquidpixel.main.interfaces;

import com.liquidpixel.sprite.model.GameSprite;

public interface IStorageItem {

    int getQuantity();
    void setQuantity(int quantity);
    int getStackSize();
    String getName();
    String getSpriteName();
    GameSprite getSprite();
    void setSprite(GameSprite sprite);

}
