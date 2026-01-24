package com.liquidpixel.main.utils.predicates;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.services.IStorageService;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.main.utils.Mappers;

import java.util.List;

public class Storage {

    IStorageItem item;
    IStorageService storageService;
    IWorldMap worldMap;


    public Storage(IStorageItem item, IStorageService storageService) {
        this.item = item;
        this.storageService = storageService;
    }

    public Storage(IStorageItem item, IWorldMap worldMap) {
        this.item = item;
        this.worldMap = worldMap;
    }

    public boolean hasAnySpace(Entity entity) {
        StorageComponent storageComponent = Mappers.storage.get(entity);
        return storageService.hasAnySpace(storageComponent, item);
    }

    public boolean hasItem(Entity entity) {
        StorageComponent storageComponent = Mappers.storage.get(entity);
        return storageService.isItemQuantityAvailable(storageComponent, item);
    }

    public boolean hasAnyQuantityOfItem(Entity entity) {
        StorageComponent storageComponent = Mappers.storage.get(entity);
        try {
            return storageService.isItemQuantityAvailable(storageComponent, new StorageItem(item.getName(), 1, item.getStackSize(), item.getSprite()));
        } catch (Exception e) {
            return false;
        }
    }

    public void setItem(IStorageItem item) {
        this.item = item;
    }

    public void setStorageService(IStorageService storageService) {
        this.storageService = storageService;
    }

    public void setWorldMap(IWorldMap worldMap) {
        this.worldMap = worldMap;
    }

    public boolean filterByEmptyStacks(GridPoint2 position) {
        List<Entity> entities = worldMap.getEntitiesAtPosition(position);
        return entities.isEmpty();
    }

    private boolean filterByIncompleteStacks(Entity entity) {
        ItemComponent itemComponent = Mappers.item.get(entity);
        if (itemComponent == null) return false;
        int stackSize = itemComponent.getStackSize();
        int quantity = itemComponent.getQuantity();

        return itemComponent.getName().equals(item.getName()) && stackSize > quantity;
    }

    public boolean filterByIncompleteStacks(GridPoint2 position) {
        List<Entity> entities = worldMap.getEntitiesAtPosition(position);
        if (entities.isEmpty()) return false;
        return entities
            .stream()
            .anyMatch(this::filterByIncompleteStacks);
    }
}
