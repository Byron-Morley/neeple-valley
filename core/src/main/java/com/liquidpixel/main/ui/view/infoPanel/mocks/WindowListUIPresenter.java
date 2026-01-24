package com.liquidpixel.main.ui.view.infoPanel.mocks;

import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WindowListUIPresenter extends VisTable {
    private final TabbedPane tabbedPane;
    private Map<WindowListUI.WindowCategory, List<String>> itemsByCategory;
    private final Map<WindowListUI.WindowCategory, WindowListTabContent> tabContents = new HashMap<>();
    private final VisTable containerTable;
    private int lastActiveTabIndex = 0;
    private final VisTable contentTable;
    private boolean tabsInitialized = false;

    public WindowListUIPresenter() {
        super();
        tabbedPane = new TabbedPane();
        tabbedPane.addListener(new TabbedPaneAdapter() {
            @Override
            public void switchedTab(Tab tab) {
                lastActiveTabIndex = tabbedPane.getTabs().indexOf(tab, true);
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

        setBackground("blue-main");

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
     * Initialize all tabs once during construction
     */
    private void initializeTabs() {
        // Create tabs for each WindowCategory
        for (WindowListUI.WindowCategory category : WindowListUI.WindowCategory.values()) {
            WindowListTabContent tabContent = new WindowListTabContent(category);
            tabContents.put(category, tabContent);

            // Add the tab with its content
            tabbedPane.add(new WindowListTab(category, tabContent));
        }

        // Set the first tab as active by default
        if (!tabbedPane.getTabs().isEmpty()) {
            tabbedPane.switchTab(tabbedPane.getTabs().get(0));
        }

        // If we already have data, update the content
        if (itemsByCategory != null) {
            updateAllTabContents();
            updateContent();
        }
    }

    public void setItems(Map<WindowListUI.WindowCategory, List<String>> itemsByCategory) {
        this.itemsByCategory = itemsByCategory;

        // Only update if tabs are already initialized
        if (tabsInitialized) {
            updateAllTabContents();
            updateContent();
        }
    }

    /**
     * Update the content of all tabs with current data
     */
    private void updateAllTabContents() {
        if (itemsByCategory == null) return;

        for (WindowListUI.WindowCategory category : WindowListUI.WindowCategory.values()) {
            WindowListTabContent tabContent = tabContents.get(category);
            if (tabContent != null) {
                List<String> items = itemsByCategory.get(category);
                tabContent.updateContent(items);
            }
        }
    }

    public void updateContent() {
        if (!tabsInitialized) return;

        if (tabbedPane.getActiveTab() instanceof WindowListTab) {
            WindowListTab windowListTab = (WindowListTab) tabbedPane.getActiveTab();
            WindowListUI.WindowCategory category = windowListTab.getCategory();
            WindowListTabContent tabContent = tabContents.get(category);

            if (tabContent != null) {
                // Clear the content table and add the active tab's content
                contentTable.clear();
                contentTable.add(tabContent.getContentTable()).grow().top();

                // Force layout updates
                contentTable.invalidateHierarchy();
                contentTable.validate();
            }
        }
    }
}
