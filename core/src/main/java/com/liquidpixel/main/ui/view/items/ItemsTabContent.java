package com.liquidpixel.main.ui.view.items;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.liquidpixel.main.listeners.ScrollPaneListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.List;

public class ItemsTabContent {
    private final String category;
    private final VisTable contentTable;
    private final VisTable itemListTable;
    private final ScrollPane scrollPane;
    private final VisLabel debugLabel;

    public ItemsTabContent(String category) {
        this.category = category;
        this.contentTable = new VisTable();
        this.itemListTable = new VisTable();

        // Ensure content table aligns to top
        contentTable.top();

        // Add debug visuals
        contentTable.setDebug(false);
        itemListTable.setDebug(false);

        // Create debug label
        debugLabel = new VisLabel("Debug: Items tab content initialized");
        debugLabel.setColor(Color.GREEN);

        // Create header
        VisTable headerTable = new VisTable();
        headerTable.add(new VisLabel(category + " Items")).expandX().left().padLeft(10);
        headerTable.add(debugLabel).right().padRight(10);

        // Create scroll pane for item list
        scrollPane = new VisScrollPane(itemListTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.addListener(new ScrollPaneListener());

        // Ensure item list table aligns to top
        itemListTable.top();

        // Set up layout with explicit sizing and top alignment
        contentTable.add(headerTable).growX().height(30).padBottom(5).top().row();
        contentTable.add(scrollPane).grow().minHeight(350).top().row();

        // Initialize with empty message
        updateContent((List<Entity>) null);
    }

    public void updateContent(List<Entity> entities) {
        itemListTable.clear();

        // Ensure item list table aligns to top
        itemListTable.top();

        int itemCount = (entities == null) ? 0 : entities.size();
        debugLabel.setText("Items: " + itemCount);

        if (entities == null || entities.isEmpty()) {
            // Create a container table for the empty message to ensure it has proper size
            VisTable emptyContainer = new VisTable();
            emptyContainer.setDebug(false);
            emptyContainer.setBackground("window-bg");

            VisLabel emptyLabel = new VisLabel("No " + category + " items found");
            emptyLabel.setColor(Color.YELLOW);

            emptyContainer.add(emptyLabel).center().expand();

            // Add the container with fixed height to ensure visibility
            itemListTable.add(emptyContainer).growX().height(60).pad(10).top().row();
        } else {
            for (Entity entity : entities) {
                ItemItemUI itemUI = new ItemItemUI(entity);
                itemUI.setDebug(false);
                itemListTable.add(itemUI).growX().padBottom(2).top().row();
            }
        }

        if (scrollPane != null && scrollPane.getStage() != null) {
            scrollPane.getStage().setScrollFocus(scrollPane);
        }

        // Ensure proper layout updates
        itemListTable.invalidateHierarchy();
        scrollPane.invalidateHierarchy();
        contentTable.invalidateHierarchy();

        // Force layout and size calculation
        itemListTable.validate();
        scrollPane.validate();
        contentTable.validate();
    }

    public Table getContentTable() {
        return contentTable;
    }
}
