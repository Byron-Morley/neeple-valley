package com.liquidpixel.main.ui.view.infoPanel.mocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.liquidpixel.main.listeners.ScrollPaneListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.List;

public class WindowListTabContent {
    private final WindowListUI.WindowCategory category;
    private final VisTable contentTable;
    private final VisTable itemListTable;
    private final ScrollPane scrollPane;
    private final VisLabel debugLabel;

    public WindowListTabContent(WindowListUI.WindowCategory category) {
        this.category = category;
        this.contentTable = new VisTable();
        this.itemListTable = new VisTable();

        // Ensure content table aligns to top
        contentTable.top();

        // Add debug visuals
        contentTable.setDebug(false);
        itemListTable.setDebug(false);

        // Create debug label
        debugLabel = new VisLabel("Debug: Tab content initialized");
        debugLabel.setColor(Color.GREEN);

        // Create header
        VisTable headerTable = new VisTable();
        headerTable.add(new VisLabel(category.name() + " Items")).expandX().left().padLeft(10);
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
        updateContent(null);
    }

    public void updateContent(List<String> items) {
        itemListTable.clear();

        // Ensure item list table aligns to top
        itemListTable.top();

        int itemCount = (items == null) ? 0 : items.size();
        debugLabel.setText("Items: " + itemCount);

        if (items == null || items.isEmpty()) {
            // Create a container table for the empty message to ensure it has proper size
            VisTable emptyContainer = new VisTable();
            emptyContainer.setDebug(false);
            emptyContainer.setBackground("window-bg");

            VisLabel emptyLabel = new VisLabel("No " + category.name() + " items");
            emptyLabel.setColor(Color.YELLOW);

            emptyContainer.add(emptyLabel).center().expand();

            // Add the container with fixed height to ensure visibility
            itemListTable.add(emptyContainer).growX().height(100).pad(10).top().row();
        } else {
            for (String item : items) {
                WindowListItemUI itemUI = new WindowListItemUI(item);
                itemUI.setDebug(false);
                itemListTable.add(itemUI).growX().height(60).padBottom(5).top().row();
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
