package com.liquidpixel.main.systems.farming;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.liquidpixel.core.components.core.StatusComponent;
import com.liquidpixel.main.components.items.FarmComponent;
import com.liquidpixel.main.components.items.GrowableComponent;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.main.components.sprite.RefreshSpriteRequirementComponent;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.engine.GameClock;
import com.liquidpixel.main.factories.ModelFactory;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.interfaces.services.IStorageService;
import com.liquidpixel.main.model.item.Item;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.sprite.api.services.IAnimationService;
import com.liquidpixel.sprite.model.GameSprite;
import com.liquidpixel.core.core.Action;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;
import com.liquidpixel.sprite.services.AnimationService;

import java.util.Iterator;
import java.util.List;

import static com.liquidpixel.main.utils.utils.getFilenameFromPath;

public class GrowSystem extends IteratingSystem {

    IStorageService storageService;
    ISpriteFactory spriteFactory;

    public GrowSystem(IStorageService storageService, ISpriteFactory spriteFactory) {
        super(Family.all(FarmComponent.class).get());
        this.storageService = storageService;
        this.spriteFactory = spriteFactory;
    }

    @Override
    protected void processEntity(Entity farm, float deltaTime) {
        FarmComponent farmComponent = Mappers.farm.get(farm);

        if (farmComponent.getCurrentCrop() != null) {
            List<Entity> crops = farmComponent.getCrops();

            Iterator<Entity> cropIterator = crops.iterator();
            while (cropIterator.hasNext()) {
                Entity crop = cropIterator.next();
                if (Mappers.growable.has(crop)) {
                    // if the farm has a worker, add this later
                    updateGrowth(crop, farm, cropIterator);
                }
            }
        }
    }

    private void updateGrowth(Entity crop, Entity farm, Iterator<Entity> cropIterator) {
        GrowableComponent growableComponent = Mappers.growable.get(crop);
        // Get current game time in milliseconds
        long currentGameTimeMillis = GameClock.getTotalGameTimeMillis();

        // Convert growth time from hours to milliseconds
        long requiredGrowthTimeMillis = GameClock.gameHoursToMillis(growableComponent.getGrowthTime());

        // Calculate elapsed time since planting
        long elapsedTimeSincePlanting = currentGameTimeMillis - growableComponent.getPlantedTime();

        // Check if current growth time is below the required growth time
        if (elapsedTimeSincePlanting < requiredGrowthTimeMillis) {
            // Update current growth time with elapsed time
            growableComponent.setCurrentGrowthTime(elapsedTimeSincePlanting);

            int step = getCurrentGrowthStep(growableComponent);
            if (growableComponent.getCurrentStep() != step) {
                growableComponent.setCurrentStep(step);
                StatusComponent statusComponent = Mappers.status.get(crop);
                statusComponent.setAction(Action.GROWING);

                IAnimationService animationService = new AnimationService(crop);
                animationService.stopAnimation();
                animationService.setCurrentFrame(step);
                crop.add(new RefreshSpriteRequirementComponent());
            }


        } else {
            // Growth is complete
            growableComponent.setCurrentGrowthTime(requiredGrowthTimeMillis);
            onGrowthComplete(crop, farm, cropIterator);
        }
    }

    private void onGrowthComplete(Entity crop, Entity farm, Iterator<Entity> cropIterator) {

        // Handle growth completion
        StorageComponent storageComponent = Mappers.storage.get(farm);

        ItemComponent itemComponent = Mappers.item.get(crop);
        String itemName = "resources/"+ getFilenameFromPath(itemComponent.getName());

        Item item = ModelFactory.getItemsModel().get(itemName);
        GameSprite sprite = spriteFactory.getSprite(item.getSpriteName(), 0);
        IStorageItem storageItem = new StorageItem(itemName, 1, item.getStackSize(), sprite);

        storageService.addItem(storageComponent, storageItem);

        // Safely remove from the list using iterator
        cropIterator.remove();

        // Remove from engine
        getEngine().removeEntity(crop);
    }

    // Helper method to get current growth progress (0.0 to 1.0)
    public float getGrowthProgress(GrowableComponent growableComponent) {
        long requiredGrowthTimeMillis = GameClock.gameHoursToMillis(growableComponent.getGrowthTime());
        return Math.min(1.0f, (float) growableComponent.getCurrentGrowthTime() / requiredGrowthTimeMillis);
    }

    // Helper method to get current growth step based on progress
    public int getCurrentGrowthStep(GrowableComponent growableComponent) {
        float progress = getGrowthProgress(growableComponent);
        int[] steps = growableComponent.getSteps();

        if (progress >= 1.0f) {
            return steps[steps.length - 1]; // Return final step
        }

        int stepIndex = (int) (progress * steps.length);
        stepIndex = Math.min(stepIndex, steps.length - 1);

        return steps[stepIndex];
    }
}
