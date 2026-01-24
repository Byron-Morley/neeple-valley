package com.liquidpixel.main.interfaces;

import com.badlogic.gdx.math.GridPoint2;

public interface ITerrainItemService {
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
}
