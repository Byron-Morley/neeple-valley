package com.liquidpixel.main.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.ai.pathfinding.MapGraph;
import com.liquidpixel.main.ai.pathfinding.PathFinderResult;
import com.liquidpixel.main.ai.tasks.leaves.BuildChunkTask;
import com.liquidpixel.main.builder.ChunkBuilder;
import com.liquidpixel.main.engine.GameState;
import com.liquidpixel.main.generators.map.WorldMap;
import com.liquidpixel.main.interfaces.*;
import com.liquidpixel.main.interfaces.managers.IMapManager;
import com.liquidpixel.main.interfaces.managers.ITaskManager;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.main.interfaces.services.ITerrainService;
import com.liquidpixel.main.listeners.TerrainMapReadyListener;
import com.liquidpixel.main.managers.TerrainItemManager;
import com.liquidpixel.main.modules.ChunkModule;
import com.liquidpixel.main.modules.RiverModule;
import com.liquidpixel.main.utils.LoopUtils;
import com.liquidpixel.main.utils.predicates.Sorter;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;

import java.util.*;
import java.util.stream.Collectors;

import static com.liquidpixel.main.managers.core.MapManager.RIVER_ON;
import static com.liquidpixel.main.managers.core.MapManager.TIME_PER_TILE;
import static com.liquidpixel.main.screens.WorldScreen.WORLD_HEIGHT;
import static com.liquidpixel.main.screens.WorldScreen.WORLD_WIDTH;
import static com.liquidpixel.main.utils.utils.getAdjacentCoordinates;

public class MapService extends Service implements IMapService, Initializable {

    ITaskManager taskManager;
    IMapManager mapManager;
    IWorldMap worldMap;
    ITerrainService terrainService;
    ITerrainItemManager terrainItemManager;
    ChunkBuilder chunkBuilder;
    MapGraph graph;
    List<GridPoint2> riverPath;
    private Set<GridPoint2> chunksWithTerrainItems;

    public MapService(IMapManager mapManager, ITaskManager taskManager, IItemService itemService, ITerrainService terrainService, ISpriteFactory spriteFactory) {
        this.taskManager = taskManager;
        this.mapManager = mapManager;
        this.terrainService = terrainService;
        this.worldMap = mapManager.getWorldMap();
        this.graph = (MapGraph) worldMap;
        chunkBuilder = new ChunkBuilder(new ChunkModule(terrainService), mapManager.getWorldMap(), itemService, spriteFactory);
        this.chunksWithTerrainItems = new HashSet<>();
        terrainItemManager = new TerrainItemManager(worldMap, itemService, taskManager, chunkBuilder);
    }

    public void init() {
        worldMap.addReadyListener(new TerrainMapReadyListener() {
            @Override
            public void onTerrainMapReady() {
                if (RIVER_ON) {
                    riverGeneration();
                }
                //TODO terrain generation
            }
        });
        createAllChunks();
    }

    private void riverGeneration() {
        RiverModule riverModule = new RiverModule();
        riverModule.generateRiverPath(WORLD_WIDTH, WORLD_HEIGHT, 3, 1, 10.0f);
        riverPath = riverModule.getRiverPath();
        applyRiverToWorldMap(riverPath, GameState.getSeed());
    }


    public void reinitRiverGen() {
        applyRiverToWorldMap(riverPath, GameState.getSeed());
    }

    /**
     * Updates the world map with water terrain for each point in the river path
     * and surrounding points to create a river with naturally varying banks
     *
     * @param riverPath The list of points that form the river path
     * @param seed      The seed to use for random generation to ensure consistency
     */
    public void applyRiverToWorldMap(List<GridPoint2> riverPath, long seed) {
        // Define the min and max river width (how many cells wide the river should be)
        int minRiverWidth = 1;
        int maxRiverWidth = 2;

        // Track which points we've already processed to avoid duplicates
        Set<GridPoint2> processedPoints = new HashSet<>();

        // Random object for generating river width changes - use the same seed
        Random random = new Random(seed);

        // Start with deterministic left and right bank positions
        // This allows asymmetrical river banks but will be the same for the same seed
        int leftBank = -random.nextInt(maxRiverWidth / 2) - 1;  // Negative value
        int rightBank = random.nextInt(maxRiverWidth / 2) + 1;  // Positive value

        // For each point in the river path
        for (GridPoint2 point : riverPath) {
            // Randomly decide if we should change the left bank (15% chance)
            if (random.nextFloat() < 0.15f) {
                // Randomly decide to move bank inward or outward by 1
                int change = (random.nextBoolean()) ? 1 : -1;
                leftBank += change;

                // Ensure minimum river width is maintained
                if (rightBank - leftBank < minRiverWidth) {
                    leftBank = rightBank - minRiverWidth;
                }

                // Ensure maximum river width is not exceeded
                if (rightBank - leftBank > maxRiverWidth) {
                    leftBank = rightBank - maxRiverWidth;
                }
            }

            // Randomly decide if we should change the right bank (15% chance)
            if (random.nextFloat() < 0.15f) {
                // Randomly decide to move bank inward or outward by 1
                int change = (random.nextBoolean()) ? 1 : -1;
                rightBank += change;

                // Ensure minimum river width is maintained
                if (rightBank - leftBank < minRiverWidth) {
                    rightBank = leftBank + minRiverWidth;
                }

                // Ensure maximum river width is not exceeded
                if (rightBank - leftBank > maxRiverWidth) {
                    rightBank = leftBank + maxRiverWidth;
                }
            }

            // Apply water to points between the left and right banks
            for (int dx = leftBank; dx <= rightBank; dx++) {
                for (int dy = leftBank; dy <= rightBank; dy++) {
                    // Create a new point with the offset
                    GridPoint2 waterPoint = new GridPoint2(point.x + dx, point.y + dy);

                    // Skip if we've already processed this point
                    if (processedPoints.contains(waterPoint)) {
                        continue;
                    }

                    // Mark as processed
                    processedPoints.add(new GridPoint2(waterPoint.x, waterPoint.y));

                    // Update the terrain to water, but only if the point is within the world bounds
                    if (worldMap.isInBounds(waterPoint)) {
                        worldMap.updateTerrain(waterPoint, "shallow-water");
                    }
                }
            }
        }

        Gdx.app.log("RiverGeneration", "Applied river to world map with " +
            processedPoints.size() + " water cells using seed: " + seed);
    }


    private void createAllChunks() {
        LoopUtils.insideOut(worldMap.getMapConfiguration().getChunkCountX(), worldMap.getMapConfiguration().getChunkCountY(), (coordinate) -> {
            createChunk(coordinate);
        });
    }


    private void createChunk(GridPoint2 location) {
        MapConfiguration mapConfiguration = worldMap.getMapConfiguration();
        BuildChunkTask buildChunkTask = new BuildChunkTask(
            chunkBuilder,
            location,
            mapConfiguration.getChunkWidth(),
            mapConfiguration.getChunkHeight()
        );

        taskManager.getTaskRunner().addToQueue(buildChunkTask);
    }


    public GridPoint2 worldToChunkPosition(GridPoint2 worldPosition) {
        MapConfiguration config = worldMap.getMapConfiguration();
        int chunkX = worldPosition.x / config.getChunkWidth();
        int chunkY = worldPosition.y / config.getChunkHeight();
        return new GridPoint2(chunkX, chunkY);
    }


    public List<GridPoint2> getWalkableAdjacentNodes(int x, int y, int width, int height) {
        List<GridPoint2> adjacentCoordinates = getAdjacentCoordinates(x, y, width, height);
        return adjacentCoordinates
            .stream()
            .filter(this::isWalkableNode)
            .collect(Collectors.toList());
    }

    public List<GridPoint2> getClosestWalkableAdjacentNodes(Vector2 origin, int x, int y, int width, int height) {
        Sorter sorter = new Sorter(origin);
        List<GridPoint2> adjacentCoordinates = getAdjacentCoordinates(x, y, width, height);
        return adjacentCoordinates
            .stream()
            .filter(this::isWalkableNode)
            .sorted(sorter::sortByCloseness)
            .collect(Collectors.toList());
    }

    public List<GridPoint2> getWalkableAdjacentNodes(GridPoint2 position) {
        return getWalkableAdjacentNodes(position.x, position.y, 1, 1);
    }

    @Override
    public WorldMap getWorldMap() {
        return (WorldMap) worldMap;
    }

    @Override
    public ITerrainItemService getTerrainItemService() {
        return terrainItemManager.getTerrainItemService();
    }

    public boolean isWalkableNode(GridPoint2 position) {
        try {
            return graph.getNode(position).isWalkable();
        } catch (NullPointerException e) {
            return false;
        }
    }

    public GridPoint2 findValidInteractionPoint(GridPoint2 from, GridPoint2 to) {
        List<GridPoint2> nodes = getClosestWalkableAdjacentNodes(new Vector2(from.x, from.y), to.x, to.y, 1, 1);
        for (GridPoint2 node : nodes) {
            PathFinderResult result = mapManager.getPathfindingService().searchNodePath(from, node);
            if (result.isFound()) {
                return node;
            }
        }
        return null;
    }

    public boolean isInHomeArea(GridPoint2 position) {
        List<GridPoint2> homeArea = worldMap.getMapConfiguration().getHomeArea();
        return (homeArea.contains(worldMap.globalToChunkCoordinates(position)));
    }

    @Override
    public PathfindingService getPathfindingService() {
        return mapManager.getPathfindingService();
    }

    public void reset() {
        worldMap.reset();
        mapManager.reset();
        chunkBuilder.setModule(new ChunkModule(terrainService));

        init();
    }

    public List<GridPoint2> buildWayPoints(GridPoint2 from, PathFinderResult pathFinderResult) {
        List<GridPoint2> waypoints = new ArrayList<>();
        waypoints.add(from);
        for (int i = 0; i < pathFinderResult.getPath().getCount(); i++) {
            waypoints.add(pathFinderResult.getPath().get(i).getLocation());
        }
        return waypoints;
    }

    public PathFinderResult searchNodePath(GridPoint2 from, GridPoint2 to) {
        return searchNodePath(from, to, true);
    }

    public PathFinderResult searchNodePath(GridPoint2 from, GridPoint2 to, boolean inBetween) {

        if (inBetween) {
            List<GridPoint2> fromWalkableAdjacentNodes = getWalkableAdjacentNodes(from);
            List<GridPoint2> toWalkableAdjacentNodes = getWalkableAdjacentNodes(to);

            PathFinderResult bestPath = null;
            float runningTotal = Float.MAX_VALUE;
            for (GridPoint2 fromAdjacentNode : fromWalkableAdjacentNodes) {
                for (GridPoint2 toAdjacentNode : toWalkableAdjacentNodes) {
                    PathFinderResult result = getPathfindingService().searchNodePath(fromAdjacentNode, toAdjacentNode);
                    if (result.isFound()) {
                        if (result.getTotalCost() < runningTotal) {
                            bestPath = result;
                            runningTotal = result.getTotalCost();
                        }
                    }
                }
            }
            return bestPath;
        } else {
            return getPathfindingService().searchNodePath(from, to);
        }
    }

    public float calculateTimeToTravel(GridPoint2 from, GridPoint2 to) {
        try {
            PathFinderResult pathFinderResult = searchNodePath(from, to);
            if (pathFinderResult.isFound()) {
                return pathFinderResult.getTotalCost() * TIME_PER_TILE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 60f;
    }


}
