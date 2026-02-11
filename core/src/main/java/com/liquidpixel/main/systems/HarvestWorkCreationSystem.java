package com.liquidpixel.main.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.liquidpixel.main.components.HarvestableComponent;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.helpers.WorkOrderHelper;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.work.IWorkOrder;
import com.liquidpixel.main.model.storage.WorkOrder;
import com.liquidpixel.main.utils.Mappers;


import java.util.List;

import static com.liquidpixel.main.model.Work.HARVEST;
import static com.liquidpixel.main.utils.utils.getSettlementFromAsset;

public class HarvestWorkCreationSystem extends IntervalIteratingSystem {

    IItemService itemService;

    public HarvestWorkCreationSystem(IItemService itemService) {
        super(Family.all(HarvestableComponent.class, ItemComponent.class).get(), 2f);
        this.itemService = itemService;
    }

    @Override
    protected void processEntity(Entity entity) {
        SettlementComponent settlement = getSettlementFromAsset(entity);
        if (Mappers.mark.has(entity) || settlement != null) {
            if (!WorkOrderHelper.doesOrderAlreadyExist(entity)) {
                IWorkOrder workOrder = new WorkOrder(entity, entity, itemService.getStorageItem(entity), HARVEST);
                settlement.addWorkOrder(workOrder);
            }
        } else {
            // outside settlement range
            // send to console
            // report this to the workshop so the user can fix
        }
    }
}
