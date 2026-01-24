package com.liquidpixel.main.model.terrain;

/**
 * Represents the possible states of a terrain item.
 */
public enum TerrainItemState {
    REMOVED,    // The item has been removed (e.g., tree cut down)
    HARVESTED,  // The item has been harvested (e.g., berries picked)
    DAMAGED,    // The item has been damaged but not destroyed
    GROWN,      // The item has grown to a new stage
    // Add more states as needed
}
