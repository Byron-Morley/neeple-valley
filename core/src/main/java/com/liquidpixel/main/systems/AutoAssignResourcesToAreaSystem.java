package com.liquidpixel.main.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.utils.Mappers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.liquidpixel.main.screens.WorldScreen.WORLD_HEIGHT;
import static com.liquidpixel.main.screens.WorldScreen.WORLD_WIDTH;
import static com.liquidpixel.main.utils.utils.getList;

public class AutoAssignResourcesToAreaSystem extends IntervalIteratingSystem {

    private boolean firstUpdate = true;

    public AutoAssignResourcesToAreaSystem() {
        super(Family.all(SettlementComponent.class).get(), 5f);
    }

    @Override
    public void update(float deltaTime) {
        if (firstUpdate) {
            // Process all entities immediately on first update
            for (Entity entity : getEntities()) {
                processEntity(entity);
            }
            firstUpdate = false;
        }

        // Continue with normal interval processing
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity) {
        SettlementComponent settlement = Mappers.settlement.get(entity);
        List<Entity> assets = settlement.getAssets()
            .stream()
            .filter(Mappers.storage::has)
            .toList();

        // Convert assets to a Set for O(1) lookups
        Set<Entity> assetSet = new HashSet<>(assets);

        List<Entity> resources = getList(getEngine().getEntitiesFor(Family.all(StorageComponent.class).get()));

        // Calculate the center rectangle bounds (32x32 tiles at the center)
        int centerX = WORLD_WIDTH / 2;
        int centerY = WORLD_HEIGHT / 2;
        int halfRectSize = 16; // Half of 32

        int minX = centerX - halfRectSize;
        int maxX = centerX + halfRectSize;
        int minY = centerY - halfRectSize;
        int maxY = centerY + halfRectSize;

        // Filter out resources that exist in assets and keep only those within the center rectangle
        List<Entity> uniqueResources = resources.stream()
            .filter(resource -> !assetSet.contains(resource))
            .filter(resource -> {
                GridPoint2 position = Mappers.position.get(resource).getGridPosition();
                return position.x >= minX && position.x < maxX &&
                    position.y >= minY && position.y < maxY;
            })
            .toList();

        for (Entity resource : uniqueResources) {
            settlement.addAsset(resource, entity);
        }
    }
}
