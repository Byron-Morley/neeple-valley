package com.liquidpixel.main.generators.map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.ai.pathfinding.FlatTiledNode;
import com.liquidpixel.main.ai.pathfinding.MapGraph;
import com.liquidpixel.main.engine.GameState;
import com.liquidpixel.main.generators.procedural.Tile;
import com.liquidpixel.main.interfaces.*;
import com.liquidpixel.main.interfaces.managers.IChunkManager;
import com.liquidpixel.main.interfaces.services.ITerrainService;
import com.liquidpixel.main.listeners.TerrainMapReadyListener;
import com.liquidpixel.main.listeners.WorldMapReadyListener;
import com.liquidpixel.main.model.sprite.NodeType;
import com.liquidpixel.main.model.terrain.*;
import com.liquidpixel.main.renderers.ChunkRenderer;
import com.liquidpixel.main.utils.IndexCalculator;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.liquidpixel.main.model.sprite.NodeType.TILE_FLOOR;
import static com.liquidpixel.main.model.sprite.NodeType.TILE_WALL;


public class WorldMap extends MapGraph implements Renderable, Initializable, IWorldMap, IChunkManager, INoiseModule {

    List<WorldMapReadyListener> readyListeners = new ArrayList<>();
    List<TerrainMapReadyListener> terrainListeners = new ArrayList<>();
    MapConfiguration mapConfiguration;
    ITerrainService terrainService;
    ChunkRenderer chunkRenderer;
    ISpriteFactory spriteFactory;
    boolean ready = false;
    GridPoint2[] gridPointRef;
    Initializable level;
    List<GridPoint2> homeArea;
    int chunkBuilt;
    Map<GridPoint2, String> terrainLedger;

    private TerrainItemLedger terrainItemLedger = new TerrainItemLedger();


    public WorldMap(MapConfiguration mapConfiguration, Initializable level, ITerrainService terrainService, ISpriteFactory spriteFactory) {
        super(mapConfiguration.getWorldWidth(), mapConfiguration.getWorldHeight());
        this.terrainService = terrainService;
        this.mapConfiguration = mapConfiguration;
        this.level = level;
        this.spriteFactory = spriteFactory;
        chunkBuilt = 0;
        chunkRenderer = new ChunkRenderer(this, spriteFactory);
        setupGridPointReference();
        copyHomeArea();
        terrainLedger = new HashMap<>();
    }

    private void setupGridPointReference() {

        gridPointRef = new GridPoint2[8];
        gridPointRef[0] = new GridPoint2(0, -1);
        gridPointRef[1] = new GridPoint2(0, 1);
        gridPointRef[2] = new GridPoint2(-1, 0);
        gridPointRef[3] = new GridPoint2(1, 0);
        gridPointRef[4] = new GridPoint2(-1, -1);
        gridPointRef[5] = new GridPoint2(-1, 1);
        gridPointRef[6] = new GridPoint2(1, -1);
        gridPointRef[7] = new GridPoint2(1, 1);
    }

    @Override
    public void init() {
    }

    public void copyHomeArea() {
        homeArea = new ArrayList<>();
        homeArea.addAll(mapConfiguration.getHomeArea());
    }

    @Override
    public void render(float delta) {
        chunkRenderer.render(delta);
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    public void addReadyListener(WorldMapReadyListener listener) {
        readyListeners.add(listener);
    }

    public void removeReadyListener(WorldMapReadyListener listener) {
        readyListeners.remove(listener);
    }

    @Override
    public void addReadyListener(TerrainMapReadyListener listener) {
        terrainListeners.add(listener);
    }

    @Override
    public void removeReadyListener(TerrainMapReadyListener listener) {
        terrainListeners.remove(listener);
    }

    public void createDirtPatch(GridPoint2 globalLocation, int width, int height) {

        String terrainType = "dirt";
        IndexCalculator indexCalculator = new IndexCalculator(this);
        TerrainDefinition terrain = terrainService.getTerrainTypeByName(terrainType);


        // Main area
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                GridPoint2 pos = new GridPoint2(globalLocation.x + x, globalLocation.y + y);
                terrainGen(pos, terrain, indexCalculator);
            }
        }

        // Random edges - first layer (20% chance)
        for (int x = -1; x <= width; x++) {
            if (Math.random() < 0.20) terrainGen(new GridPoint2(globalLocation.x + x, globalLocation.y - 1), terrain, indexCalculator);
            if (Math.random() < 0.20) terrainGen(new GridPoint2(globalLocation.x + x, globalLocation.y + height), terrain, indexCalculator);
        }
        for (int y = -1; y <= height; y++) {
            if (Math.random() < 0.20) terrainGen(new GridPoint2(globalLocation.x - 1, globalLocation.y + y), terrain, indexCalculator);
            if (Math.random() < 0.20) terrainGen(new GridPoint2(globalLocation.x + width, globalLocation.y + y), terrain, indexCalculator);
        }

        // Random edges - second layer (5% chance)
        for (int x = -2; x <= width + 1; x++) {
            if (Math.random() < 0.05) terrainGen(new GridPoint2(globalLocation.x + x, globalLocation.y - 2), terrain, indexCalculator);
            if (Math.random() < 0.05) terrainGen(new GridPoint2(globalLocation.x + x, globalLocation.y + height + 1), terrain, indexCalculator);
        }
        for (int y = -2; y <= height + 1; y++) {
            if (Math.random() < 0.05) terrainGen(new GridPoint2(globalLocation.x - 2, globalLocation.y + y), terrain, indexCalculator);
            if (Math.random() < 0.05) terrainGen(new GridPoint2(globalLocation.x + width + 1, globalLocation.y + y), terrain, indexCalculator);
        }

        for (int x = 0; x < width + 4; x++) {
            for (int y = 0; y < height + 4; y++) {
                GridPoint2 pos = new GridPoint2(globalLocation.x + x - 2, globalLocation.y + y - 2);
                adjacentTileCalculation(pos, indexCalculator, terrain.getBaseTile());
            }
        }
    }

    @Override
    public void addEntity(Entity entity, GridPoint2 point) {
        try {
            FlatTiledNode node = nodes.get(new GridPoint2(point));
            if (node != null) {
                node.addEntity(entity);
            } else {
                System.out.println("WARNING: Node is null for point " + point);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    @Override
    public void removeEntity(Entity entity, GridPoint2 point) {
        try {
            nodes.get(point).removeEntity(entity);
        } catch (Exception e) {
            //continue
//            System.out.println("Error adding entity to node: " + point);
        }
    }

    @Override
    public List<Entity> getEntitiesAtPosition(GridPoint2 point) {
        return nodes.get(point).getEntities();
    }

    @Override
    public Entity getSelectableEntityAtPosition(GridPoint2 point) {
        try {
            return nodes.get(point).getEntities().stream()
                .filter(Mappers.selection::has)
                .reduce((first, second) -> second)
                .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public Entity getEntityAtPosition(GridPoint2 point) {
        try {
            return nodes.get(point).getEntities().stream()
                .reduce((first, second) -> second)
                .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isBuildable(GridPoint2 point) {
        try {
            FlatTiledNode node = nodes.get(point);
            return (node.getEntities().isEmpty() && node.isWalkable());
        } catch (Exception e) {
            return false;
        }
    }

    private void terrainGen(GridPoint2 position, TerrainDefinition terrain, IndexCalculator indexCalculator) {
        GridPoint2 chunkPosition = globalToChunkCoordinates(position);
        Chunk chunk = chunkRenderer.getChunks().get(chunkPosition);
        setTileTerrain(position, chunk, terrain, GameState.getCurrentTerrainId(), indexCalculator);
    }


    @Override
    public void updateTerrain(GridPoint2 globalLocation, String terrainType) {
        TerrainDefinition terrain = terrainService.getTerrainTypeByName(terrainType);
        this.updateTerrain(globalLocation, terrainType, GameState.getCurrentTerrainId());
    }

    @Override
    public void updateTerrain(GridPoint2 globalLocation, String terrainType, String baseTile) {

        GridPoint2 chunkPosition = globalToChunkCoordinates(globalLocation);
        Chunk chunk = chunkRenderer.getChunks().get(chunkPosition);

        IndexCalculator indexCalculator = new IndexCalculator(this);
        TerrainDefinition terrain = terrainService.getTerrainTypeByName(terrainType);

        if (setTileTerrain(globalLocation, chunk, terrain, baseTile, indexCalculator)) {
            adjacentTileCalculation(globalLocation, indexCalculator, baseTile);
            terrainLedger.put(globalLocation, terrainType);
//            System.out.println("Updating terrain at " + globalLocation.x + " " + globalLocation.y);
        }
    }

    @Override
    public boolean isInBounds(GridPoint2 position) {
        return position.x >= 0 && position.x < width &&
            position.y >= 0 && position.y < height;
    }


    private boolean setTileTerrain(GridPoint2 globalLocation, Chunk chunk, TerrainDefinition terrain, String baseTile, IndexCalculator indexCalculator) {
        Tile tile = chunk.getTile(globalLocation);

        if (tile.getTerrain().getId() == terrain.getId() && tile.getTerrain().getBaseTile().equals(baseTile)) {
            return false;
        } else {
            tile.setTerrain(terrain);
            int index = indexCalculator.calculateIndex(globalLocation.x, globalLocation.y, terrain);
            TextureRegion textureRegion = spriteFactory.getTextureWithFallback(GameState.getCurrentTerrainId(), index);
            tile.setTextureRegion(textureRegion);
            nodes.get(globalLocation).setType(NodeType.fromValue(terrain.getTypeId()));
            return true;
        }
    }

    private void adjacentTileCalculation(GridPoint2 globalLocation, IndexCalculator indexCalculator, String baseTile) {
//        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};  // X offsets
//        int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};  // Y offsets

        int[] dx = {-1, 0, -1};    // X offsets
        int[] dy = {-1, -1, 0};     // Y offsets


        for (int i = 0; i < dy.length; i++) {
            int x = globalLocation.x + dx[i];
            int y = globalLocation.y + dy[i];

            GridPoint2 newGlobalLocation = new GridPoint2(x, y);
            GridPoint2 chunkPosition = globalToChunkCoordinates(newGlobalLocation);
            Chunk chunk = chunkRenderer.getChunks().get(chunkPosition);

            if (chunk != null) {
                try {
                    Tile tile = chunk.getTile(newGlobalLocation);
                    TerrainDefinition terrain = tile.getTerrain();
                    int index = indexCalculator.calculateIndex(x, y, terrain);
                    TextureRegion adjTextureRegion = spriteFactory.getTextureWithFallback(baseTile, index);
                    tile.setTextureRegion(adjTextureRegion);
                    nodes.get(new GridPoint2(x, y)).setType(NodeType.fromValue(terrain.getTypeId()));
                } catch (Exception e) {
                    // Skip this tile and continue with the next one
                    continue;
                }
            }
        }
    }


    @Override
    public TerrainDefinition getTerrain(int x, int y) {

        try {
            GridPoint2 globalLocation = new GridPoint2(x, y);
            GridPoint2 chunkPosition = globalToChunkCoordinates(globalLocation);
            Chunk chunk = chunkRenderer.getChunks().get(chunkPosition);
            Tile tile = chunk.getTile(globalLocation);
            return tile.getTerrain();

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void addChunk(GridPoint2 location, Chunk chunk) {
        chunkRenderer.addChunk(location, chunk);
        checkHomeAreaIsBuilt(location);
        chunkBuilt++;
        if (chunkBuilt == mapConfiguration.getChunkCount() && !ready) {
            System.out.println("World Map Ready");
            //TODO probably need to chain the listeners in a better way
            terrainListeners.forEach(TerrainMapReadyListener::onTerrainMapReady);
            readyListeners.forEach(WorldMapReadyListener::onWorldMapReady);
            ready = true;
            level.init();
        }
    }

    private void checkHomeAreaIsBuilt(GridPoint2 location) {
        if (!homeArea.isEmpty()) homeArea.remove(location);
    }

    @Override
    public void addNode(FlatTiledNode node) {
        nodes.put(node.getLocation(), node);
        addConnections(node);
    }


    public void refreshConnections(GridPoint2 location) {
        addConnections(getNode(location));
    }

    private void addConnections(FlatTiledNode node) {
        for (int i = 0; i < gridPointRef.length-1; i++) {
            FlatTiledNode adjNode = nodes.get(new GridPoint2(node.getLocation().x, node.getLocation().y).add(gridPointRef[i]));
            if (adjNode != null) {
                connectBothNodes(node, adjNode);
            }
        }
    }

    @Override
    public void setActiveChunk(Chunk activeChunk) {
        chunkRenderer.setActiveChunk(activeChunk);
    }

    public MapConfiguration getMapConfiguration() {
        return mapConfiguration;
    }

    @Override
    public void addObstacle(GridPoint2 position, int width, int height) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                updateNodeType(new GridPoint2(position.x + x, position.y + y), TILE_WALL);
            }
        }
    }

    @Override
    public void addObstacle(GridPoint2 position) {
        this.addObstacle(position, 1, 1);
    }

    @Override
    public void removeObstacle(GridPoint2 position, int width, int height) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                updateNodeType(new GridPoint2(position.x + x, position.y + y), TILE_FLOOR);
            }
        }
    }

    @Override
    public void removeObstacle(GridPoint2 position) {
        this.removeObstacle(position, 1, 1);
    }

    @Override
    public void dispose() {

    }

    public boolean isHomeArea(int globalX, int globalY) {
        return homeArea.contains(globalToChunkCoordinates(globalX, globalY));
    }

    public GridPoint2 globalToChunkCoordinates(int globalX, int globalY) {
        int chunkX = globalX / mapConfiguration.getChunkWidth();
        int chunkY = globalY / mapConfiguration.getChunkHeight();
        return new GridPoint2(chunkX, chunkY);
    }

    public GridPoint2 globalToChunkCoordinates(GridPoint2 position) {
        return globalToChunkCoordinates(position.x, position.y);
    }

    public void reinit() {
        for (Map.Entry<GridPoint2, String> entry : terrainLedger.entrySet()) {
            GridPoint2 position = entry.getKey();
            updateTerrain(position, entry.getValue());
        }
    }

    public void reset() {
        chunkRenderer.reset();
        nodes.clear();
        homeArea.clear();
        chunkBuilt = 0;
        ready = false;
    }

    @Override
    public int getVariant(TerrainDefinition terrain) {
        return terrainService.getVariant(terrain);
    }

    @Override
    public TerrainItem getItem(TerrainDefinition terrain) {
        return terrainService.getItem(terrain);
    }

    public Map<GridPoint2, String> getTerrainLedger() {
        return terrainLedger;
    }

    public void setTerrainLedger(Map<GridPoint2, String> terrainLedger) {
        this.terrainLedger = terrainLedger;
    }

    // Add these methods to access the ledger
    public TerrainItemLedger getTerrainItemLedger() {
        return terrainItemLedger;
    }

    public void setTerrainItemLedger(TerrainItemLedger terrainItemLedger) {
        this.terrainItemLedger = terrainItemLedger;
    }

    // Method to change a terrain item's state
    public void setTerrainItemState(GridPoint2 position, TerrainItemState state) {
        terrainItemLedger.setItemState(position, state);
    }
}
