package com.liquidpixel.item.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.liquidpixel.main.components.*;
import com.liquidpixel.main.components.colony.BuildingComponent;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.item.factories.ItemFactory;
import com.liquidpixel.main.interfaces.managers.IItemManager;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.item.services.ItemService;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.sprite.api.factory.IAnimationFactory;
import com.liquidpixel.sprite.api.factory.ISpriteComponentFactory;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;

public class ItemManager implements EntityListener, IItemManager {

    ItemFactory itemFactory;
    Engine engine;
    IItemService itemService;

    public ItemManager(
        ISpriteComponentFactory spriteComponentFactory,
        IAnimationFactory animationFactory,
        ISpriteFactory spriteFactory
    ) {
        itemService = new ItemService(this, spriteFactory);
        itemFactory = new ItemFactory(spriteComponentFactory, animationFactory, spriteFactory);
        engine = GameResources.get().getEngine();
    }

    @Override
    public void entityAdded(Entity entity) {
    }

    @Override
    public void entityRemoved(Entity entity) {
        if (Mappers.foundation.has(entity)) {
            FoundationComponent foundationComponent = Mappers.foundation.get(entity);
            for (Entity fence : foundationComponent.getFences()) {
                engine.removeEntity(fence);
            }
        }

        if (Mappers.building.has(entity)) {
            BuildingComponent buildingComponent = Mappers.building.get(entity);
            if (buildingComponent.getDoor() != null) {
                engine.removeEntity(buildingComponent.getDoor());
            }
        }

    }

    @Override
    public IItemService getItemService() {
        return itemService;
    }

    @Override
    public ItemFactory getItemFactory() {
        return itemFactory;
    }
}
