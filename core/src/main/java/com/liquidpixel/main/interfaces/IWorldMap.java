package com.liquidpixel.main.interfaces;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.ai.pathfinding.FlatTiledNode;
import com.liquidpixel.main.interfaces.MapConfiguration;
import com.liquidpixel.main.listeners.TerrainMapReadyListener;
import com.liquidpixel.main.listeners.WorldMapReadyListener;
import com.liquidpixel.main.model.sprite.NodeType;
import com.liquidpixel.main.model.terrain.TerrainDefinition;
import com.liquidpixel.main.model.terrain.TerrainItemLedger;
import com.liquidpixel.main.model.terrain.TerrainItemState;

import java.util.List;
import java.util.Map;


public interface IWorldMap {
    boolean isHomeArea(int globalX, int globalY);

    GridPoint2 globalToChunkCoordinates(int globalX, int globalY);

    GridPoint2 globalToChunkCoordinates(GridPoint2 position);

    MapConfiguration getMapConfiguration();

    void addObstacle(GridPoint2 position, int width, int height);

    void addObstacle(GridPoint2 position);

    void removeObstacle(GridPoint2 position, int width, int height);

    void removeObstacle(GridPoint2 position);

    void createDirtPatch(GridPoint2 globalLocation, int width, int height);

    void addEntity(Entity entity, GridPoint2 point);

    void updateNodeType(GridPoint2 point, NodeType nodeType);

    FlatTiledNode getNode(GridPoint2 point);

    void removeEntity(Entity entity, GridPoint2 point);

    List<Entity> getEntitiesAtPosition(GridPoint2 point);

    Entity getSelectableEntityAtPosition(GridPoint2 point);

    boolean isBuildable(GridPoint2 point);

    void reset();

    void addReadyListener(WorldMapReadyListener listener);

    void removeReadyListener(WorldMapReadyListener listener);

    void addReadyListener(TerrainMapReadyListener listener);

    void removeReadyListener(TerrainMapReadyListener listener);

    void updateTerrain(GridPoint2 position, String terrain);

    void updateTerrain(GridPoint2 position, String terrain, String baseTile);

    boolean isInBounds(GridPoint2 position);

    void setTerrainLedger(Map<GridPoint2, String> terrainLedger);

    Map<GridPoint2, String> getTerrainLedger();

    void reinit();

    void init();

    boolean isReady();

    void render(float scaledDelta);

    void dispose();

    TerrainItemLedger getTerrainItemLedger();

    void setTerrainItemState(GridPoint2 position, TerrainItemState state);

    void removeNodeConnections(FlatTiledNode sourceNode);

    void refreshConnections(GridPoint2 location);
}
