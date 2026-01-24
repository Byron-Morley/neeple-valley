package com.liquidpixel.main.systems.settlement;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.FoundationComponent;
import com.liquidpixel.main.components.storage.ConsumerComponent;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.IRecipe;
import com.liquidpixel.main.interfaces.services.ISettlementService;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.utils.Mappers;

import java.util.List;
import java.util.Map;

public class BuildingSystem extends IteratingSystem {
    private final IItemService itemService;
    ISettlementService settlementService;

    public BuildingSystem(IItemService itemService, ISettlementService settlementService) {
        super(Family.all(FoundationComponent.class, ConsumerComponent.class, StorageComponent.class).get());
        this.itemService = itemService;
        this.settlementService = settlementService;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        FoundationComponent foundation = Mappers.foundation.get(entity);

        StorageComponent storage = Mappers.storage.get(entity);
        ConsumerComponent consumer = Mappers.consumer.get(entity);

        boolean canBuild = isRecipeFulfilled(consumer, storage);

        if (canBuild) {
            GridPoint2 position = Mappers.position.get(entity).getGridPosition();
            if (position != null) {
                IRecipe recipe = consumer.getRecipe();
                List<IStorageItem> outputs = recipe.getOutput();

                for (IStorageItem outputItem : outputs) {
                    for (int i = 0; i < outputItem.getQuantity(); i++) {
                        settlementService.buildInSettlement(Mappers.asset.get(entity).getSettlement(), outputItem.getName(), position);
                    }
                }

                consumeRecipeInputs(recipe, storage);
                entity.remove(ConsumerComponent.class);
                entity.remove(StorageComponent.class);
                getEngine().removeEntity(entity);
            }
        }
    }

    private boolean isRecipeFulfilled(ConsumerComponent consumer, StorageComponent storage) {
        IRecipe recipe = consumer.getRecipe();
        if (recipe == null) {
            return false;
        }

        Map<String, IStorageItem> availableItems = storage.getItems();

        List<IStorageItem> requiredInputs = recipe.getInput();
        for (IStorageItem requiredItem : requiredInputs) {
            String itemId = requiredItem.getName();
            int requiredAmount = requiredItem.getQuantity();

            IStorageItem storageItem = availableItems.get(itemId);
            if (storageItem == null || storageItem.getQuantity() < requiredAmount) {
                return false;
            }
        }

        return true;
    }


    private void consumeRecipeInputs(IRecipe recipe, StorageComponent storage) {
        Map<String, IStorageItem> availableItems = storage.getItems();

        List<IStorageItem> requiredInputs = recipe.getInput();
        for (IStorageItem requiredItem : requiredInputs) {
            String itemId = requiredItem.getName();
            int requiredAmount = requiredItem.getQuantity();

            IStorageItem storageItem = availableItems.get(itemId);
            if (storageItem != null) {
                storageItem.setQuantity(storageItem.getQuantity() - requiredAmount);

                if (storageItem.getQuantity() <= 0) {
                    availableItems.remove(itemId);
                }
            }
        }
    }
}
