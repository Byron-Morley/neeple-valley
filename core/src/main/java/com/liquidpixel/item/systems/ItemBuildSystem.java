package com.liquidpixel.item.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.item.components.RefreshItemBuildComponent;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;

public class ItemBuildSystem extends IteratingSystem {

    IItemService itemService;
    ISpriteFactory spriteFactory;

    public ItemBuildSystem(IItemService itemService, ISpriteFactory spriteFactory) {
        super(Family.all(ItemComponent.class, RefreshItemBuildComponent.class).get());
        this.itemService = itemService;
        this.spriteFactory = spriteFactory;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (Mappers.item.has(entity)) {
            ItemComponent itemComponent = Mappers.item.get(entity);
            if (itemComponent.getSprite() == null) {
                itemComponent.setSprite(spriteFactory.getSprite(itemComponent.getName()));
            }
        }
        entity.remove(RefreshItemBuildComponent.class);
    }
}
