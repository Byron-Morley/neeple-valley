package com.liquidpixel.main.systems.farming;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.liquidpixel.main.components.items.FarmComponent;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.services.FarmService;
import com.liquidpixel.main.utils.Mappers;

public class FarmSystem extends IteratingSystem {

    IItemService itemService;

    public FarmSystem(IItemService itemService) {
        super(Family.all(FarmComponent.class).get());
        this.itemService = itemService;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        FarmComponent farmComponent = Mappers.farm.get(entity);

        if(farmComponent.isPlanted() && farmComponent.isEmpty()){
            FarmService farmService = new FarmService(itemService);
            Entity crop = farmComponent.getCropsToPlant().get(0);
            ItemComponent cropComponent = Mappers.item.get(crop);

            farmService.plantCropInFarm(cropComponent.getName(), entity);
        }
    }
}
