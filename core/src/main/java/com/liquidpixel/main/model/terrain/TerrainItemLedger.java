package com.liquidpixel.main.model.terrain;

import com.badlogic.gdx.math.GridPoint2;
import java.util.HashMap;
import java.util.Map;

/**
 * Tracks changes to procedurally generated terrain items.
 */
public class TerrainItemLedger {
    // Map of position to item state
    private Map<Long, TerrainItemState> itemStates = new HashMap<>();

    /**
     * Records the state of a terrain item at the given position.
     * @param position The position of the terrain item
     * @param state The state to record
     */
    public void setItemState(GridPoint2 position, TerrainItemState state) {
        long key = positionToKey(position);
        itemStates.put(key, state);
    }

    /**
     * Gets the state of a terrain item at the given position.
     * @param position The position to check
     * @return The state of the item, or null if no state is recorded
     */
    public TerrainItemState getItemState(GridPoint2 position) {
        long key = positionToKey(position);
        return itemStates.get(key);
    }

    /**
     * Checks if a terrain item at the given position has a recorded state.
     */
    public boolean hasItemState(GridPoint2 position) {
        long key = positionToKey(position);
        return itemStates.containsKey(key);
    }

    /**
     * Converts a position to a unique key.
     */
    private long positionToKey(GridPoint2 position) {
        return ((long)position.x << 32) | (position.y & 0xFFFFFFFFL);
    }

    /**
     * Gets the map of item states.
     */
    public Map<Long, TerrainItemState> getItemStates() {
        return itemStates;
    }

    /**
     * Sets the map of item states.
     */
    public void setItemStates(Map<Long, TerrainItemState> itemStates) {
        this.itemStates = itemStates;
    }
}
