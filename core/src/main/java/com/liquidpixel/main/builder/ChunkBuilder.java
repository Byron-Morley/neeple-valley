package com.liquidpixel.main.builder;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.liquidpixel.main.ai.pathfinding.FlatTiledNode;
import com.liquidpixel.main.ai.pathfinding.MapGraph;
import com.liquidpixel.main.components.BodyComponent;
import com.liquidpixel.main.components.TerrainItemComponent;
import com.liquidpixel.main.components.load.DontSaveComponent;
import com.liquidpixel.main.engine.GameState;
import com.liquidpixel.main.generators.procedural.Tile;
import com.liquidpixel.main.interfaces.*;
import com.liquidpixel.main.interfaces.managers.IChunkManager;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.model.sprite.NodeType;
import com.liquidpixel.main.model.terrain.*;
import com.liquidpixel.main.utils.IndexCalculator;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.liquidpixel.main.utils.Dimensions.PX_PER_METER;

public class ChunkBuilder implements IChunkBuilder {

    INoiseModule module;
    IChunkManager chunkManager;
    IWorldMap worldMap;
    IItemService itemService;
    IndexCalculator indexCalculator;

    ISpriteFactory spriteFactory;

    // Cache to store terrain item decisions for each position
    private final Map<Long, TerrainItemPlacement> terrainItemCache = new HashMap<>();

    public ChunkBuilder(INoiseModule module, IWorldMap worldMap, IItemService itemService, ISpriteFactory spriteFactory) {
        this.module = module;
        this.worldMap = worldMap;
        this.chunkManager = (IChunkManager) worldMap;
        this.itemService = itemService;
        this.spriteFactory = spriteFactory;
        indexCalculator = new IndexCalculator(module);
    }

    @Override
    public void buildChunk(GridPoint2 location, int width, int height) {
        // Existing code unchanged
        Chunk chunk = new Chunk(location, width, height, (int) PX_PER_METER, (int) PX_PER_METER);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int dx = location.x * width + x;
                int dy = location.y * height + y;

                TerrainDefinition terrain = module.getTerrain(dx, dy);
                int index = indexCalculator.calculateIndex(dx, dy, terrain);
                TextureRegion textureRegion = spriteFactory.getTextureWithFallback(GameState.getCurrentTerrainId(), index);

                if (textureRegion != null) {
                    chunk.setCell(x, y, new Tile(textureRegion, terrain));
                }

                int typeId = terrain.getTypeId();
                if (typeId == 2) {
                    FlatTiledNode leftNode = ((MapGraph) worldMap).getNode(new GridPoint2(dx - 1, dy));
                    FlatTiledNode bottomNode = ((MapGraph) worldMap).getNode(new GridPoint2(dx, dy - 1));
                    FlatTiledNode bottomLeftNode = ((MapGraph) worldMap).getNode(new GridPoint2(dx - 1, dy - 1));

                    clearNode(leftNode);
                    clearNode(bottomNode);
                    clearNode(bottomLeftNode);
                }

                FlatTiledNode node = new FlatTiledNode(
                    dx, dy,
                    NodeType.fromValue(typeId),
                    8,
                    worldMap.getMapConfiguration().getWorldWidth() * dy + dx
                );
                chunkManager.addNode(node);
            }
        }
        chunkManager.addChunk(location, chunk);
    }

    private void clearNode(FlatTiledNode node) {
        if (node == null) return;
        node.setType(NodeType.TILE_WALL);
        ((IWorldMap) worldMap).removeNodeConnections(node);
    }

    @Override
    public void spawnTerrainItems(GridPoint2 location, int width, int height) {
        TerrainItemLedger ledger = worldMap.getTerrainItemLedger();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int dx = location.x * width + x;
                int dy = location.y * height + y;
                GridPoint2 worldPos = new GridPoint2(dx, dy);

                // Check if this position has a recorded state
                if (ledger.hasItemState(worldPos)) {
                    TerrainItemState state = ledger.getItemState(worldPos);

                    // Handle the item based on its state
                    handleTerrainItemState(worldPos, state);
                } else {
                    // This position hasn't been modified, generate terrain items normally
                    TerrainDefinition terrain = module.getTerrain(dx, dy);

                    if (terrain.getTerrainItems() != null && !worldMap.isHomeArea(dx, dy)) {
                        // Use deterministic approach to decide if we should spawn an item
                        TerrainItemPlacement placement = getTerrainItemPlacement(dx, dy);

                        if (placement != null && placement.shouldSpawn) {
                            calculateSpawn(placement.item, dx, dy);
                        }
                    }
                }
            }
        }
    }

    /**
     * Handles a terrain item based on its recorded state.
     */
    private void handleTerrainItemState(GridPoint2 position, TerrainItemState state) {
        // You can implement different behaviors based on the state
        switch (state) {
            case REMOVED:
                // Don't spawn anything
                break;
            case HARVESTED:
                // Maybe spawn a harvested version
                // spawnHarvestedItem(position.x, position.y);
                break;
            case DAMAGED:
                // Spawn a damaged version
                // spawnDamagedItem(position.x, position.y);
                break;
            case GROWN:
                // Spawn a grown version
                // spawnGrownItem(position.x, position.y);
                break;
            // Add more cases as needed
        }
    }


    private TerrainItemPlacement getTerrainItemPlacement(int dx, int dy) {
        // Create a unique key for this position
        long positionKey = ((long) dx << 32) | (dy & 0xFFFFFFFFL);

        // Check if we've already made a decision for this position
        if (terrainItemCache.containsKey(positionKey)) {
            return terrainItemCache.get(positionKey);
        }

        // Get terrain at this position
        TerrainDefinition terrain = module.getTerrain(dx, dy);

        // If no terrain items or in home area, don't spawn
        if (terrain.getTerrainItems() == null || worldMap.isHomeArea(dx, dy)) {
            terrainItemCache.put(positionKey, null);
            return null;
        }

        // Use a deterministic approach to decide if we should spawn an item
        // Use the position coordinates to seed the random number generator
        long seed = positionKey ^ GameState.getSeed();
        MathUtils.random.setSeed(seed);

        // Get the item based on the seeded random
        TerrainItem item = getItemDeterministically(terrain, seed);

        if (item == null) {
            terrainItemCache.put(positionKey, null);
            return null;
        }

        // Create and cache the placement decision
        TerrainItemPlacement placement = new TerrainItemPlacement(item, true);
        terrainItemCache.put(positionKey, placement);
        return placement;
    }

    private TerrainItem getItemDeterministically(TerrainDefinition terrain, long seed) {
        // If there are no terrain items, return null
        if (terrain.getTerrainItems() == null || terrain.getTerrainItems().isEmpty()) {
            return null;
        }

        // Set the random seed for deterministic selection
        MathUtils.random.setSeed(seed);

        // First, determine if we should spawn anything at all based on the highest probability
        float maxProbability = 0;
        for (TerrainItem item : terrain.getTerrainItems()) {
            maxProbability = (float) Math.max(maxProbability, item.getProbability());
        }

        // Apply a sparsity factor to further reduce density if needed
        float sparsityFactor = 1.0f; // Adjust this if you want to make items even more sparse
        float effectiveProbability = maxProbability * sparsityFactor;

        // Check if we should spawn anything at all
        if (MathUtils.random.nextFloat() > effectiveProbability) {
            return null;
        }

        // Calculate total weight for the items that can spawn
        float totalWeight = 0;
        for (TerrainItem item : terrain.getTerrainItems()) {
            totalWeight += item.getProbability();
        }

        // If total weight is 0, return null
        if (totalWeight <= 0) {
            return null;
        }

        // Select an item based on weight
        float randomValue = MathUtils.random.nextFloat() * totalWeight;
        float currentWeight = 0;

        for (TerrainItem item : terrain.getTerrainItems()) {
            currentWeight += item.getProbability();
            if (randomValue <= currentWeight) {
                return item;
            }
        }

        // Fallback (should not reach here if weights are properly set)
        return terrain.getTerrainItems().get(0);
    }


    private void calculateSpawn(TerrainItem item, int dx, int dy) {
        Entity entity = itemService.getItem(item.getName()).build();
        entity.add(new DontSaveComponent());
        entity.add(new TerrainItemComponent());

        BodyComponent bodyComponent = Mappers.body.get(entity);
        List<GridPoint2> positions;

        // If body component exists, use its positions, otherwise use a single position
        if (bodyComponent != null) {
            positions = bodyComponent.generateAbsolutePositions(new GridPoint2(dx, dy));
        } else {
            positions = List.of(new GridPoint2(dx, dy));
        }

        boolean canBuild = true;
        boolean isWaterItem = false;

        // Check if this is a water item by examining the terrain at the spawn position
        TerrainDefinition terrain = module.getTerrain(dx, dy);
        if (terrain != null && (isWaterTerrain(terrain.getName()))) {
            isWaterItem = true;
        }

        // Check each position and its surrounding tiles
        for (GridPoint2 pos : positions) {
            // Skip buildability check for water items on water terrain
            if (!isWaterItem && !worldMap.isBuildable(pos)) {
                canBuild = false;
                break;
            }

            // For water items, we need to ensure they're only placed on water
            if (isWaterItem) {
                TerrainDefinition posTerrain = module.getTerrain(pos.x, pos.y);
                if (posTerrain == null || !isWaterTerrain(posTerrain.getName())) {
                    canBuild = false;
                    break;
                }
            }

            // Check surrounding tiles for each position
            // For water items, we don't need to check surrounding tiles
            if (!isWaterItem) {
                for (int offsetX = -1; offsetX <= 1; offsetX++) {
                    for (int offsetY = -1; offsetY <= 1; offsetY++) {
                        if (offsetX == 0 && offsetY == 0) continue;
                        if (!worldMap.isBuildable(new GridPoint2(pos.x + offsetX, pos.y + offsetY))) {
                            canBuild = false;
                            break;
                        }
                    }
                    if (!canBuild) break;
                }
            }
        }

        if (canBuild) {
            itemService.spawnItem(entity, new GridPoint2(dx, dy));
        } else {
            itemService.dispose(entity);
        }
    }

    /**
     * Helper method to check if a terrain name represents water terrain.
     *
     * @param terrainName the name of the terrain to check
     * @return true if the terrain is water (shallow or deep), false otherwise
     */
    private boolean isWaterTerrain(String terrainName) {
        return "shallow-water".equals(terrainName) || "deep-water".equals(terrainName);
    }


    public void setModule(INoiseModule module) {
        this.module = module;
        indexCalculator = new IndexCalculator(module);
        // Clear the cache when the module changes
        terrainItemCache.clear();
    }

    // Helper class to store terrain item placement decisions
    private record TerrainItemPlacement(TerrainItem item, boolean shouldSpawn) {
    }

    // Method to clear the cache (useful when changing worlds or resetting)
    public void clearCache() {
        terrainItemCache.clear();
    }
}
