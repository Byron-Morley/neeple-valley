package com.liquidpixel.main.providers;

import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.sprite.api.provider.ISpriteComponentProvider;
import com.liquidpixel.sprite.api.component.ISpriteComponent;

public class GameSpriteProvider implements ISpriteComponentProvider {

    IItemService itemService;

    public GameSpriteProvider(IItemService itemService) {
        this.itemService = itemService;
    }

    @Override
    public ISpriteComponent getSpriteComponent(String name, int mod) {
        return itemService.getSpriteComponent(name, mod);
    }
}
