package com.liquidpixel.main.interfaces;

import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.interfaces.ITerrainItemService;

public interface ITerrainItemManager {
    /**
     * Creates terrain items in chunks visible from the camera position
     * @param cameraChunkPosition The chunk position of the camera
     */
    void createVisibleTerrainItems(GridPoint2 cameraChunkPosition);

    /**
     * Culls terrain items outside the visible range
     * @param cameraChunkPosition The chunk position of the camera
     */
    void cullTerrainItems(GridPoint2 cameraChunkPosition);

    /**
     * Creates terrain items in a specific chunk
     * @param location The chunk location
     */
    void createTerrainItems(GridPoint2 location);

    /**
     * Removes terrain items from a specific chunk
     * @param chunkPos The chunk position
     */
    void removeTerrainItems(GridPoint2 chunkPos);

    /**
     * Converts a world position to a chunk position
     * @param worldPosition The world position
     * @return The chunk position
     */
    GridPoint2 worldToChunkPosition(GridPoint2 worldPosition);

    /**
     * Creates terrain items in all chunks (for initial loading)
     */
    void createAllTerrainItems();

    /**
     * Clears all terrain items from the world
     */
    void clearAllTerrainItems();

    ITerrainItemService getTerrainItemService();
}
