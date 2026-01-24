package com.liquidpixel.main.ui.view.agent;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.liquidpixel.main.listeners.ScrollPaneListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.List;

public class AgentTabContent {
    private final VisTable contentTable;
    private final VisTable agentListTable;
    private final ScrollPane scrollPane;
    private final VisLabel debugLabel;

    public AgentTabContent() {
        this.contentTable = new VisTable();
        this.agentListTable = new VisTable();

        // Ensure content table aligns to top
        contentTable.top();

        // Add debug visuals
        contentTable.setDebug(false);
        agentListTable.setDebug(false);

        // Create debug label
        debugLabel = new VisLabel("Debug: Agent tab content initialized");
        debugLabel.setColor(Color.GREEN);

        // Create header
        VisTable headerTable = new VisTable();
        headerTable.add(new VisLabel("All Agents")).expandX().left().padLeft(10);
        headerTable.add(debugLabel).right().padRight(10);

        // Create scroll pane for agent list
        scrollPane = new VisScrollPane(agentListTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.addListener(new ScrollPaneListener());

        // Ensure agent list table aligns to top
        agentListTable.top();

        // Set up layout with explicit sizing and top alignment
        contentTable.add(headerTable).growX().height(30).padBottom(5).top().row();
        contentTable.add(scrollPane).grow().minHeight(350).top().row();

        // Initialize with empty message
        updateContent(null);
    }

    public void updateContent(List<Entity> agents) {
        agentListTable.clear();

        // Ensure agent list table aligns to top
        agentListTable.top();

        int itemCount = (agents == null) ? 0 : agents.size();
        debugLabel.setText("Agents: " + itemCount);

        if (agents == null || agents.isEmpty()) {
            // Create a container table for the empty message to ensure it has proper size
            VisTable emptyContainer = new VisTable();
            emptyContainer.setDebug(false);
            emptyContainer.setBackground("window-bg");

            VisLabel emptyLabel = new VisLabel("No agents found");
            emptyLabel.setColor(Color.YELLOW);

            emptyContainer.add(emptyLabel).center().expand();

            // Add the container with fixed height to ensure visibility
            agentListTable.add(emptyContainer).growX().height(60).pad(10).top().row();
        } else {
            for (Entity agent : agents) {
                AgentItemUI agentItem = new AgentItemUI(agent);
                agentItem.setDebug(false);
                // Remove fixed height and let items size themselves
                agentListTable.add(agentItem).growX().padBottom(2).top().row();
            }
        }

        if (scrollPane != null && scrollPane.getStage() != null) {
            scrollPane.getStage().setScrollFocus(scrollPane);
        }

        // Ensure proper layout updates
        agentListTable.invalidateHierarchy();
        scrollPane.invalidateHierarchy();
        contentTable.invalidateHierarchy();

        // Force layout and size calculation
        agentListTable.validate();
        scrollPane.validate();
        contentTable.validate();
    }

    public Table getContentTable() {
        return contentTable;
    }
}
