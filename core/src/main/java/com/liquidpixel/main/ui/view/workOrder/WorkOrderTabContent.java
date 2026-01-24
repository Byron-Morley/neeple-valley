package com.liquidpixel.main.ui.view.workOrder;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.liquidpixel.main.interfaces.work.IWorkOrder;
import com.liquidpixel.main.listeners.ScrollPaneListener;
import com.liquidpixel.main.model.storage.WorkOrder;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.List;

public class WorkOrderTabContent {
    private final WorkOrder.State state;
    private final VisTable contentTable;
    private final VisTable orderListTable;
    private final ScrollPane scrollPane;
    private final VisLabel debugLabel;

    public WorkOrderTabContent(WorkOrder.State state) {
        this.state = state;
        this.contentTable = new VisTable();
        this.orderListTable = new VisTable();

        // Ensure content table aligns to top
        contentTable.top();

        // Add debug visuals
        contentTable.setDebug(false);
        orderListTable.setDebug(false);

        // Create debug label
        debugLabel = new VisLabel("Debug: Tab content initialized");
        debugLabel.setColor(Color.GREEN);

        // Create header
        VisTable headerTable = new VisTable();
        headerTable.add(new VisLabel(state.name() + " Work Orders")).expandX().left().padLeft(10);
        headerTable.add(debugLabel).right().padRight(10);

        // Create scroll pane for order list
        scrollPane = new VisScrollPane(orderListTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.addListener(new ScrollPaneListener());

        // Ensure order list table aligns to top
        orderListTable.top();

        // Set up layout with explicit sizing and top alignment
        contentTable.add(headerTable).growX().height(30).padBottom(5).top().row();
        contentTable.add(scrollPane).grow().minHeight(350).top().row();

        // Initialize with empty message
        updateContent(null);
    }

    public void updateContent(List<IWorkOrder> orders) {
        orderListTable.clear();

        // Ensure order list table aligns to top
        orderListTable.top();

        int itemCount = (orders == null) ? 0 : orders.size();
        debugLabel.setText("Items: " + itemCount);

        if (orders == null || orders.isEmpty()) {
            // Create a container table for the empty message to ensure it has proper size
            VisTable emptyContainer = new VisTable();
            emptyContainer.setDebug(false);
            emptyContainer.setBackground("window-bg");

            VisLabel emptyLabel = new VisLabel("No " + state.name() + " work orders");
            emptyLabel.setColor(Color.YELLOW);

            emptyContainer.add(emptyLabel).center().expand();

            // Add the container with fixed height to ensure visibility
            orderListTable.add(emptyContainer).growX().height(100).pad(10).top().row();
        } else {
            for (IWorkOrder order : orders) {
                WorkOrderItemUI orderItem = new WorkOrderItemUI(order);
                orderItem.setDebug(false);
                orderListTable.add(orderItem).growX().height(100).padBottom(5).top().row();
            }
        }

        if (scrollPane != null && scrollPane.getStage() != null) {
            scrollPane.getStage().setScrollFocus(scrollPane);
        }

        // Ensure proper layout updates
        orderListTable.invalidateHierarchy();
        scrollPane.invalidateHierarchy();
        contentTable.invalidateHierarchy();

        // Force layout and size calculation
        orderListTable.validate();
        scrollPane.validate();
        contentTable.validate();
    }

    public Table getContentTable() {
        return contentTable;
    }
}
