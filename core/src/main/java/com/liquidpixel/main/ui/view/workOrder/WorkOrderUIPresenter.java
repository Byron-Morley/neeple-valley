package com.liquidpixel.main.ui.view.workOrder;

import com.liquidpixel.main.interfaces.work.IWorkOrder;
import com.liquidpixel.main.model.storage.WorkOrder;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkOrderUIPresenter extends VisTable {
    private final TabbedPane tabbedPane;
    private Map<WorkOrder.State, List<IWorkOrder>> workOrdersByState;
    private final Map<WorkOrder.State, WorkOrderTabContent> tabContents = new HashMap<>();
    private final VisTable containerTable;
    private int lastActiveTabIndex = 0;
    private final VisTable contentTable;
    private boolean tabsInitialized = false;

    public WorkOrderUIPresenter() {
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

        // Add the tabbed pane with a fixed height of 60 pixels
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
     * Initialize all tabs once during construction
     */
    private void initializeTabs() {
        // Create tabs for each WorkOrder.WorkState
        for (WorkOrder.State state : WorkOrder.State.values()) {
            WorkOrderTabContent tabContent = new WorkOrderTabContent(state);
            tabContents.put(state, tabContent);

            // Add the tab with its content
            tabbedPane.add(new WorkOrderTab(state, tabContent));
        }

        // Set the first tab as active by default
        if (!tabbedPane.getTabs().isEmpty()) {
            tabbedPane.switchTab(tabbedPane.getTabs().get(0));
        }

        // If we already have data, update the content
        if (workOrdersByState != null) {
            updateAllTabContents();
            updateContent();
        }
    }

    public void setWorkOrders(Map<WorkOrder.State, List<IWorkOrder>> workOrdersByState) {
        this.workOrdersByState = workOrdersByState;

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
        if (workOrdersByState == null) return;

        for (WorkOrder.State state : WorkOrder.State.values()) {
            WorkOrderTabContent tabContent = tabContents.get(state);
            if (tabContent != null) {
                List<IWorkOrder> orders = workOrdersByState.get(state);
                tabContent.updateContent(orders);
            }
        }
    }

    public void updateContent() {
        if (!tabsInitialized) return;

        if (tabbedPane.getActiveTab() instanceof WorkOrderTab) {
            WorkOrderTab workOrderTab = (WorkOrderTab) tabbedPane.getActiveTab();
            WorkOrder.State state = workOrderTab.getState();
            WorkOrderTabContent tabContent = tabContents.get(state);

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
