package com.liquidpixel.main.utils.predicates;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.components.HarvestableComponent;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.main.utils.Mappers;

import java.util.List;
import java.util.Map;

public class Resources {

    Map<String, StorageItem> requiredResources;

    public Resources(Map<String, StorageItem> requiredResources) {
        this.requiredResources = requiredResources;
    }

    public boolean hasCorrectResources(Entity entity) {

        HarvestableComponent harvestableComponent = Mappers.harvest.get(entity);
        List<IStorageItem> items = harvestableComponent.getRecipe().getOutput();

        return itemsMatch(items);
    }


    private boolean itemsMatch(List<IStorageItem> items) {
        for (IStorageItem item : items) {
            if (requiredResources.containsKey(item.getName())) {
                return true;
            }
        }
        return false;
    }
}
