package com.liquidpixel.main.ui.view.items;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.main.utils.Mappers;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;

import java.util.*;

public class ItemsUIPresenter extends VisTable {
    private final TabbedPane tabbedPane;
    private List<Entity> itemEntities;
    private final Map<String, ItemsTabContent> tabContents = new HashMap<>();
    private final VisTable containerTable;
    private final VisTable contentTable;
    private boolean tabsInitialized = false;

    public ItemsUIPresenter() {
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
     * Initialize tabs for all categories and "All Items"
     */
    private void initializeTabs() {
        if (itemEntities == null) return;

        // Collect all categories from item entities
        Set<String> categories = new LinkedHashSet<>();
        for (Entity entity : itemEntities) {
            ItemComponent itemComponent = Mappers.item.get(entity);
            if (itemComponent != null && itemComponent.getLabel() != null) {
                String category = extractCategory(itemComponent.getLabel());
                if (category != null) {
                    categories.add(category);
                }
            }
        }

        // Create "All Items" tab first
        ItemsTabContent allItemsContent = new ItemsTabContent("All Items");
        tabContents.put("All Items", allItemsContent);
        tabbedPane.add(new ItemsTab("All Items", allItemsContent));

        // Create category tabs
        for (String category : categories) {
            ItemsTabContent tabContent = new ItemsTabContent(category);
            tabContents.put(category, tabContent);
            tabbedPane.add(new ItemsTab(category, tabContent));
        }

        // Set the first tab as active by default
        if (!tabbedPane.getTabs().isEmpty()) {
            tabbedPane.switchTab(tabbedPane.getTabs().get(0));
        }

        // Update content
        updateAllTabContents();
        updateContent();
    }

    public void setItemEntities(List<Entity> entities) {
        this.itemEntities = entities;

        // If tabs are already initialized, update them
        if (tabsInitialized) {
            updateAllTabContents();
            updateContent();
        }
    }

    /**
     * Extract category from item name (e.g., "houses/door" -> "houses")
     */
    private String extractCategory(String itemName) {
        if (itemName == null || !itemName.contains("/")) {
            return "misc"; // Default category for items without category
        }
        return itemName.substring(0, itemName.indexOf("/"));
    }

    /**
     * Extract item display name (e.g., "houses/door" -> "door")
     */
    private String extractItemName(String fullName) {
        if (fullName == null || !fullName.contains("/")) {
            return fullName;
        }
        return fullName.substring(fullName.lastIndexOf("/") + 1);
    }

    /**
     * Update the content of all tabs with current data
     */
    private void updateAllTabContents() {
        if (itemEntities == null) return;

        // Update "All Items" tab
        ItemsTabContent allItemsTab = tabContents.get("All Items");
        if (allItemsTab != null) {
            allItemsTab.updateContent(itemEntities);
        }

        // Update category tabs
        for (String category : tabContents.keySet()) {
            if (!"All Items".equals(category)) {
                List<Entity> categoryEntities = new ArrayList<>();
                for (Entity entity : itemEntities) {
                    ItemComponent itemComponent = Mappers.item.get(entity);
                    if (itemComponent != null && itemComponent.getLabel() != null) {
                        if (category.equals(extractCategory(itemComponent.getLabel()))) {
                            categoryEntities.add(entity);
                        }
                    }
                }

                ItemsTabContent tabContent = tabContents.get(category);
                if (tabContent != null) {
                    tabContent.updateContent(categoryEntities);
                }
            }
        }
    }

    public void updateContent() {
        if (!tabsInitialized) return;

        if (tabbedPane.getActiveTab() instanceof ItemsTab) {
            ItemsTab itemsTab = (ItemsTab) tabbedPane.getActiveTab();
            String category = itemsTab.getCategory();
            ItemsTabContent tabContent = tabContents.get(category);

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
