package com.liquidpixel.main.results;

import com.liquidpixel.main.model.storage.WorkOrder;

import java.util.ArrayList;
import java.util.List;

public class WorkOrderResult {
    private final List<WorkOrder> newWorkOrders;
    private final boolean shouldRemove;

    public WorkOrderResult(List<WorkOrder> newWorkOrders, boolean shouldRemove) {
        this.newWorkOrders = newWorkOrders;
        this.shouldRemove = shouldRemove;
    }

    public List<WorkOrder> getNewWorkOrders() {
        return newWorkOrders;
    }

    public boolean shouldRemove() {
        return shouldRemove;
    }

    public static WorkOrderResult success() {
        return new WorkOrderResult(new ArrayList<>(), false);
    }

    public static WorkOrderResult remove() {
        return new WorkOrderResult(new ArrayList<>(), true);
    }

    public static WorkOrderResult withNewWorkOrders(List<WorkOrder> newWorkOrders) {
        return new WorkOrderResult(newWorkOrders, false);
    }
}
