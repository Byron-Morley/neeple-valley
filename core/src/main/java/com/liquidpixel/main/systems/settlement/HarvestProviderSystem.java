package com.liquidpixel.main.systems.settlement;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.liquidpixel.main.components.AssetComponent;
import com.liquidpixel.main.components.HarvestProviderComponent;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.exceptions.storage.InsufficientQuantityException;
import com.liquidpixel.main.exceptions.storage.InsufficientSpaceException;
import com.liquidpixel.item.factories.ItemFactory;
import com.liquidpixel.main.interfaces.services.IStorageService;
import com.liquidpixel.main.model.item.Item;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.sprite.model.GameSprite;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;

import static com.liquidpixel.main.engine.GameClock.SECONDS_PER_DAY;

/**
 * Periodically adds resources to the storage
 */
public class HarvestProviderSystem extends IteratingSystem {

    static final float UPDATE_INTERVAL = 2f;
    IStorageService storageService;
    ISpriteFactory spriteFactory;

    public HarvestProviderSystem(IStorageService storageService, ISpriteFactory spriteFactory) {
        super(Family.all(HarvestProviderComponent.class).get());
        this.storageService = storageService;
        this.spriteFactory = spriteFactory;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        HarvestProviderComponent provider = Mappers.harvestProvider.get(entity);
        AssetComponent building = Mappers.asset.get(entity);

        if (building == null) {
            throw new RuntimeException("HarvestProviderSystem: Building not set for harvest provider");
        }

        Entity settlementEntity = building.getSettlement();
        if (settlementEntity == null) {
            throw new RuntimeException("HarvestProviderSystem: Settlement not set for building ");
        }
        SettlementComponent settlement = Mappers.settlement.get(settlementEntity);
        settlement.setTimeSinceLastUpdate(settlement.getTimeSinceLastUpdate() + deltaTime);

        if (settlement.getTimeSinceLastUpdate() >= UPDATE_INTERVAL) {
            if (provider.isActive()) {
                if (isReadyToHarvest(provider, settlement)) {
                    harvest(entity, provider, settlement);
                } else {
                    calculateTimeLeftTillHarvest(provider, settlement);
                }
            } else {
                Item item = ItemFactory.getModels().get(provider.getResource());
                if (canBeActivated(entity, item)) {
                    activateHarvestCycle(entity, 0f);
                }
            }
            settlement.setTimeSinceLastUpdate(0f);
        }
    }

    private static void calculateTimeLeftTillHarvest(HarvestProviderComponent provider, SettlementComponent settlement) {
        float timeDifference = Math.round((provider.getTimeLeft() - settlement.getTimeSinceLastUpdate()) * 1000) / 1000f;
        provider.setTimeLeft(timeDifference);
    }

    private void harvest(Entity entity, HarvestProviderComponent provider, SettlementComponent settlement) {
        String name = provider.getResource();
        Item item = ItemFactory.getModels().get(name);

        float harvestTime = item.getHarvestTime() * SECONDS_PER_DAY * settlement.getBaseWorkerHarvestSpeed();
        float overTime = Math.abs(provider.getTimeLeft());

        int harvestCount = (int) (overTime / harvestTime) + 1;
        float leftoverTime = overTime % harvestTime;
        int quantity = harvestCount * item.getYield();

        if (quantity > 0) {
            GameSprite sprite = spriteFactory.getSprite(item.getSpriteName());
            try {
                storageService.addItem(Mappers.storage.get(entity), new StorageItem(name, quantity, item.getStackSize(), sprite));
            } catch (InsufficientQuantityException exception) {
                storageService.addItem(Mappers.storage.get(entity), new StorageItem(name, exception.getAvailable(), item.getStackSize(), sprite));
            } catch (InsufficientSpaceException exception) {
                System.out.println("Not enough space");
            }
        }

        if (canBeActivated(entity, item)) {
            activateHarvestCycle(entity, leftoverTime);
        } else {
            provider.setActive(false);
        }
    }


    private static boolean isReadyToHarvest(HarvestProviderComponent provider, SettlementComponent settlement) {
        return provider.getTimeLeft() <= 0.0001f;
    }

    private void activateHarvestCycle(Entity entity, float time) {

        SettlementComponent settlement = Mappers.settlement.get(Mappers.asset.get(entity).getSettlement());
        HarvestProviderComponent provider = Mappers.harvestProvider.get(entity);

        Item item = ItemFactory.getModels().get(provider.getResource());
        float harvestTime = item.getHarvestTime() * SECONDS_PER_DAY * settlement.getBaseWorkerHarvestSpeed();

        provider.setActive(true);
        provider.setTimeLeft(harvestTime - time);
        provider.setUnitSize(item.getUnitSize());
        calculateTimeLeftTillHarvest(provider, settlement);
    }

    private boolean canBeActivated(Entity entity, Item item) {
        if (Mappers.jobs.has(entity)) {
            if (!Mappers.jobs.get(entity).getWorkers().isEmpty() &&
                storageService.hasSpace(Mappers.storage.get(entity), new StorageItem(item.getName(), 1, item.getStackSize(), spriteFactory.getSprite(item.getSpriteName())))) {
                return true;
            }
        }
        return false;
    }
}
