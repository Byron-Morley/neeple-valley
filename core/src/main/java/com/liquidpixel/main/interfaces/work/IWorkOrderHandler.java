package com.liquidpixel.main.interfaces.work;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.interfaces.work.IWorkOrder;
import com.liquidpixel.main.results.WorkOrderResult;

public interface IWorkOrderHandler {
    WorkOrderResult process(com.liquidpixel.main.interfaces.work.IWorkOrder workOrder, Entity worker, SettlementComponent settlement);

    void completeWork(IWorkOrder workOrder);
}
