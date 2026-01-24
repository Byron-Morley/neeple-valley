package com.liquidpixel.main.ui.view.workOrder;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.main.interfaces.work.IWorkOrder;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisTable;

public class WorkOrderItemUI extends VisTable {

    private final IWorkOrder workOrder;
    private final VisProgressBar progressBar;

    public WorkOrderItemUI(IWorkOrder workOrder) {
        super();
        this.workOrder = workOrder;
        try {
            setBackground("window-bg");
        }catch(Exception e){
            System.out.println("failed to load window-bg");
        }
        pad(10);

        // Create left column with basic info
        VisTable leftColumn = new VisTable();
        leftColumn.add(new VisLabel("Item: " + workOrder.getItemName())).left().row();
        leftColumn.add(new VisLabel("Quantity: " + workOrder.getQuantity())).left().row();

        // Create right column with entity info
        VisTable rightColumn = new VisTable();

        // Origin info
        Entity origin = workOrder.getOrigin();
        String originName = getEntityName(origin);
        rightColumn.add(new VisLabel("From: " + originName)).left().row();

        // Destination info
        Entity destination = workOrder.getDestination();
        String destinationName = getEntityName(destination);
        rightColumn.add(new VisLabel("To: " + destinationName)).left().row();

        // Worker info if assigned
        Entity worker = workOrder.getWorker();
        String workerName = worker != null ? getEntityName(worker) : "Unassigned";
        rightColumn.add(new VisLabel("Worker: " + workerName)).left().row();

        // Add columns to main table
        add(leftColumn).expand().fill().pad(5);
        add(rightColumn).expand().fill().pad(5).row();
        progressBar = null;
    }

    private String getEntityName(Entity entity) {
        if (entity == null) return "Unknown";

        StringBuilder nameBuilder = new StringBuilder();

        // Get item name if available
        ItemComponent itemComponent = entity.getComponent(ItemComponent.class);
        if (itemComponent != null) {
            nameBuilder.append(itemComponent.getLabel());
        }

        // Add position if available
        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
        if (positionComponent != null) {
            // If we already added an item label, add a separator
            if (nameBuilder.length() > 0) {
                nameBuilder.append(" at ");
            }
            nameBuilder.append(positionComponent.getGridPosition());
        }

        // If neither component was found, use a default name
        if (nameBuilder.length() == 0) {
            return "Unknown";
        }

        return nameBuilder.toString();
    }
}
