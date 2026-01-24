package com.liquidpixel.main.ui.view.interfaces;

import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Interface for universal tabs that can be dynamically added/removed from windows
 */
public interface IUniversalTab {

    /**
     * Get the title that appears on the tab
     */
    String getTabTitle();

    /**
     * Get the content table for this tab
     */
    VisTable getContentTable();

    /**
     * Update the tab content - called when the tab becomes active or data changes
     */
    void updateContent();

    /**
     * Optional: Called when the tab is added to a window
     */
    default void onTabAdded() {}

    /**
     * Optional: Called when the tab is removed from a window
     */
    default void onTabRemoved() {}
}
