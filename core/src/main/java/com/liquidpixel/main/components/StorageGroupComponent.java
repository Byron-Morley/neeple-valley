package com.liquidpixel.main.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.utils.Mappers;

import java.util.HashMap;
import java.util.Map;

public class StorageGroupComponent implements Component {

    Map<GridPoint2, Entity> storageSpots;

    public StorageGroupComponent() {
        storageSpots = new HashMap<>();
    }

    public Map<GridPoint2, Entity> getStorageSpots() {
        return storageSpots;
    }

    public void addStorageSpot(GridPoint2 position, Entity entity) {
        storageSpots.put(position, entity);
    }

    public void setPriority(int priority) {
        for (Entity entity : storageSpots.values()) {
            if (Mappers.storage.has(entity)) {
                StorageComponent storage = Mappers.storage.get(entity);
                storage.setPriority(priority);
            }
        }
    }
}
