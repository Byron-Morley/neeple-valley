package com.liquidpixel.main.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.liquidpixel.main.components.BodyComponent;
import com.liquidpixel.main.components.HarvestableComponent;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.components.ui.MarkComponent;
import com.liquidpixel.main.interfaces.work.IWorkOrder;
import com.liquidpixel.main.model.storage.WorkOrder;
import com.liquidpixel.main.utils.Mappers;


import java.util.List;

import static com.liquidpixel.main.model.Work.HARVEST;
import static com.liquidpixel.main.utils.utils.getSettlementFromAsset;

public class HarvestWorkCreationSystem extends IntervalIteratingSystem {

    public HarvestWorkCreationSystem() {
        super(Family.all(HarvestableComponent.class, MarkComponent.class, ItemComponent.class, BodyComponent.class).get(), 2f);
    }

    @Override
    protected void processEntity(Entity entity) {
        SettlementComponent settlement = getSettlementFromAsset(entity);
        ItemComponent itemComponent = Mappers.item.get(entity);
        if (!doesOrderAlreadyExist(entity)) {
            IWorkOrder workOrder = new WorkOrder(entity, entity, itemComponent.getItem(), HARVEST);
            settlement.addWorkOrder(workOrder);
        }
    }

    private boolean doesOrderAlreadyExist(Entity entity) {
        SettlementComponent settlement = getSettlementFromAsset(entity);
        List<IWorkOrder> workOrders = settlement.getWorkOrders();

        return workOrders
            .stream()
            .filter(work->work.getType().equals(HARVEST))
            .filter(work->work.getOrigin().equals(entity))
            .anyMatch(IWorkOrder::isActive);
    }
}
