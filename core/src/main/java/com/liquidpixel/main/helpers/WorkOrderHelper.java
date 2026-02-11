package com.liquidpixel.main.helpers;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.interfaces.work.IWorkOrder;

import java.util.List;

import static com.liquidpixel.main.model.Work.HARVEST;
import static com.liquidpixel.main.utils.utils.getSettlementFromAsset;

public class WorkOrderHelper {

    public static boolean doesOrderAlreadyExist(Entity entity) {
        SettlementComponent settlement = getSettlementFromAsset(entity);
        List<IWorkOrder> workOrders = settlement.getWorkOrders();

        return workOrders
            .stream()
            .filter(work -> work.getType().equals(HARVEST))
            .filter(work -> work.getOrigin().equals(entity))
            .anyMatch(IWorkOrder::isActive);
    }
}
