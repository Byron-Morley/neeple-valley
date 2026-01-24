package com.liquidpixel.main.ui.view.agent;

import com.badlogic.ashley.core.Entity;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;

import java.util.List;

public class AgentUIPresenter extends VisTable {
    private final TabbedPane tabbedPane;
    private List<Entity> agents;
    private AgentTabContent tabContent;
    private final VisTable containerTable;
    private final VisTable contentTable;
    private boolean tabsInitialized = false;

    public AgentUIPresenter() {
        super();
        tabbedPane = new TabbedPane();
        tabbedPane.addListener(new TabbedPaneAdapter() {
            @Override
            public void switchedTab(Tab tab) {
                updateContent();
            }
        });

        // Create container for the tabbed pane
        containerTable = new VisTable();

        // Add the tabbed pane with a fixed height of 30 pixels
        containerTable.add(tabbedPane.getTable()).growX().height(30).row();

        // Create content area below the tabs
        contentTable = new VisTable();
        // Use top alignment for the content table
        containerTable.add(contentTable).grow().top().row();

        setBackground(VisUI.getSkin().getDrawable("white"));
        setColor(0.1f, 0.1f, 0.1f, 1f);

        // Add the container to this table with top alignment
        this.add(containerTable).grow().top();

        // We'll initialize tabs later when the component is added to the stage
    }

    @Override
    protected void setStage(com.badlogic.gdx.scenes.scene2d.Stage stage) {
        super.setStage(stage);

        // Initialize tabs once the component is added to a stage
        if (stage != null && !tabsInitialized) {
            initializeTabs();
            tabsInitialized = true;
        }
    }

    /**
     * Initialize the single agents tab
     */
    private void initializeTabs() {
        // Create a single tab for all agents
        tabContent = new AgentTabContent();

        // Add the tab with its content
        tabbedPane.add(new AgentTab(tabContent));

        // Set the first tab as active by default
        if (!tabbedPane.getTabs().isEmpty()) {
            tabbedPane.switchTab(tabbedPane.getTabs().get(0));
        }

        // If we already have data, update the content
        if (agents != null) {
            updateTabContent();
            updateContent();
        }
    }

    public void setAgents(List<Entity> agents) {
        this.agents = agents;

        // Only update if tabs are already initialized
        if (tabsInitialized) {
            updateTabContent();
            updateContent();
        }
    }

    /**
     * Update the content of the tab with current data
     */
    private void updateTabContent() {
        if (agents == null || tabContent == null) return;
        tabContent.updateContent(agents);
    }

    public void updateContent() {
        if (!tabsInitialized || tabContent == null) return;

        // Clear the content table and add the tab's content
        contentTable.clear();
        contentTable.add(tabContent.getContentTable()).grow().top();

        // Force layout updates
        contentTable.invalidateHierarchy();
        contentTable.validate();
    }
}
