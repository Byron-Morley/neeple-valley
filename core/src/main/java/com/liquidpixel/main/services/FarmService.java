package com.liquidpixel.main.services;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.items.FarmComponent;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.sprite.api.services.IAnimationService;
import com.liquidpixel.sprite.services.AnimationService;

import java.util.Iterator;
import java.util.List;

public class FarmService {

    IItemService itemService;

    public FarmService(IItemService itemService) {
        this.itemService = itemService;
    }

    public void plantCropInFarm(String cropName, Entity farm) {
        FarmComponent farmComponent = Mappers.farm.get(farm);

        GridPoint2 farmPosition = Mappers.position.get(farm).getGridPosition();
        GridPoint2 plotOrigin = farmPosition.cpy().add(farmComponent.getOrigin());

        List<GridPoint2> plots = farmComponent.getRelativePlotPoints();

        for (GridPoint2 plot : plots) {
            Entity crop = itemService.getItem(cropName, plots.size()).build();
            itemService.spawnItem(crop, plotOrigin.cpy().add(plot));
            IAnimationService animation = new AnimationService(crop);
            animation.stopAnimation();
            farmComponent.addCrop(crop);
        }

        farmComponent.setCurrentCrop(cropName);

    }

    public void removePlantFromFarm(Entity farm) {
        FarmComponent farmComponent = Mappers.farm.get(farm);
        farmComponent.removeCrop(farm);

        farmComponent.setPlanted(false);
        farmComponent.setCurrentCrop(null);
        List<Entity> crops = farmComponent.getCrops();

        Engine engine = GameResources.get().getEngine();
        farmComponent.getCropsToPlant().clear();

        Iterator<Entity> cropIterator = crops.iterator();
        while (cropIterator.hasNext()) {
            Entity crop = cropIterator.next();

            cropIterator.remove();
            engine.removeEntity(crop);
        }
    }
}
