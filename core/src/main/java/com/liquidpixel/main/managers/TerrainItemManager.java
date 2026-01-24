package com.liquidpixel.main.managers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.ai.tasks.leaves.BuildTerrainItemsTask;
import com.liquidpixel.main.builder.ChunkBuilder;
import com.liquidpixel.main.interfaces.ITerrainItemManager;
import com.liquidpixel.main.interfaces.ITerrainItemService;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.MapConfiguration;
import com.liquidpixel.main.interfaces.managers.ITaskManager;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.services.TerrainItemService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.liquidpixel.main.managers.core.MapManager.TERRAIN_ITEMS_ON;

public class TerrainItemManager implements ITerrainItemManager {
    private static final int DEFAULT_TERRAIN_ITEM_CHUNK_RADIUS = 9;

    private final IWorldMap worldMap;
    private final IItemService itemService;
    private final ITaskManager taskManager;
    private final ChunkBuilder chunkBuilder;
    ITerrainItemService terrainItemService;
    private final Set<GridPoint2> chunksWithTerrainItems;
    private final int chunkRadius;

    public TerrainItemManager(
        IWorldMap worldMap,
        IItemService itemService,
        ITaskManager taskManager,
        ChunkBuilder chunkBuilder) {
        this(worldMap, itemService, taskManager, chunkBuilder, DEFAULT_TERRAIN_ITEM_CHUNK_RADIUS);
    }

    public TerrainItemManager(
        IWorldMap worldMap,
        IItemService itemService,
        ITaskManager taskManager,
        ChunkBuilder chunkBuilder,
        int chunkRadius) {
        this.worldMap = worldMap;
        this.itemService = itemService;
        this.taskManager = taskManager;
        this.chunkBuilder = chunkBuilder;
        this.chunksWithTerrainItems = new HashSet<>();
        this.chunkRadius = chunkRadius;
        terrainItemService = new TerrainItemService(this);
    }

    @Override
    public void createVisibleTerrainItems(GridPoint2 cameraChunkPosition) {
        // Get chunks within radius of camera position
        for (int x = cameraChunkPosition.x - chunkRadius; x <= cameraChunkPosition.x + chunkRadius; x++) {
            for (int y = cameraChunkPosition.y - chunkRadius; y <= cameraChunkPosition.y + chunkRadius; y++) {
                // Skip invalid chunk coordinates
                if (x < 0 || y < 0 || x >= worldMap.getMapConfiguration().getChunkCountX()
                    || y >= worldMap.getMapConfiguration().getChunkCountY()) {
                    continue;
                }

                GridPoint2 chunkPos = new GridPoint2(x, y);
                // Only create terrain items if they don't already exist in this chunk
                if (!chunksWithTerrainItems.contains(chunkPos)) {
                    createTerrainItems(chunkPos);
                    chunksWithTerrainItems.add(new GridPoint2(chunkPos)); // Store a copy
                }
            }
        }
    }

    @Override
    public void cullTerrainItems(GridPoint2 cameraChunkPosition) {
        // Create a copy to avoid concurrent modification
        List<GridPoint2> chunksToCheck = new ArrayList<>(chunksWithTerrainItems);

        for (GridPoint2 chunkPos : chunksToCheck) {
            // Check if chunk is outside the visible range
            if (Math.abs(chunkPos.x - cameraChunkPosition.x) > chunkRadius ||
                Math.abs(chunkPos.y - cameraChunkPosition.y) > chunkRadius) {
                // Remove terrain items from this chunk
                removeTerrainItems(chunkPos);
                chunksWithTerrainItems.remove(chunkPos);
            }
        }
    }

    @Override
    public void createTerrainItems(GridPoint2 location) {
        if (TERRAIN_ITEMS_ON) {
            MapConfiguration mapConfiguration = worldMap.getMapConfiguration();
            BuildTerrainItemsTask buildTerrainItemsTask = new BuildTerrainItemsTask(
                chunkBuilder,
                location,
                mapConfiguration.getChunkWidth(),
                mapConfiguration.getChunkHeight()
            );
            taskManager.getTaskRunner().addToQueue(buildTerrainItemsTask);
        }
    }

    @Override
    public void removeTerrainItems(GridPoint2 chunkPos) {
        MapConfiguration config = worldMap.getMapConfiguration();
        int chunkWidth = config.getChunkWidth();
        int chunkHeight = config.getChunkHeight();

        // Calculate world coordinates for this chunk
        int startX = chunkPos.x * chunkWidth;
        int startY = chunkPos.y * chunkHeight;
        int endX = startX + chunkWidth;
        int endY = startY + chunkHeight;

        // Get all entities with terrain item components in this chunk area
        List<Entity> terrainEntities = itemService.getTerrainItemsInArea(
            new GridPoint2(startX, startY),
            new GridPoint2(endX, endY));

        // Dispose all terrain entities in this chunk
        for (Entity entity : terrainEntities) {
            itemService.dispose(entity);
        }
    }

    @Override
    public GridPoint2 worldToChunkPosition(GridPoint2 worldPosition) {
        MapConfiguration config = worldMap.getMapConfiguration();
        int chunkX = worldPosition.x / config.getChunkWidth();
        int chunkY = worldPosition.y / config.getChunkHeight();
        return new GridPoint2(chunkX, chunkY);
    }

    @Override
    public void createAllTerrainItems() {
        for (int x = 0; x < worldMap.getMapConfiguration().getChunkCountX(); x++) {
            for (int y = 0; y < worldMap.getMapConfiguration().getChunkCountY(); y++) {
                createTerrainItems(new GridPoint2(x, y));
            }
        }
    }

    @Override
    public void clearAllTerrainItems() {
        for (GridPoint2 chunkPos : new ArrayList<>(chunksWithTerrainItems)) {
            removeTerrainItems(chunkPos);
        }
        chunksWithTerrainItems.clear();
    }

    public ITerrainItemService getTerrainItemService() {
        return terrainItemService;
    }
}
