package com.liquidpixel.main.systems;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.colony.BuildingComponent;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.components.workshop.JobComponent;
import com.liquidpixel.main.components.workshop.WorkshopComponent;
import com.liquidpixel.main.helpers.WorkOrderHelper;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.work.IWorkOrder;
import com.liquidpixel.main.model.storage.WorkOrder;
import com.liquidpixel.main.utils.LoopUtils;
import com.liquidpixel.main.utils.Mappers;

import static com.liquidpixel.main.model.Work.HARVEST;
import static com.liquidpixel.main.utils.utils.getSettlementFromAsset;

public class WorkshopWorkCreationSystem extends IntervalIteratingSystem {

    IItemService itemService;

    public WorkshopWorkCreationSystem(IItemService itemService) {
        super(Family.all(WorkshopComponent.class, JobComponent.class).get(), 2);
        this.itemService = itemService;
    }

    @Override
    protected void processEntity(Entity workshopEntity) {
//        System.out.println("WorkshopWorkCreationSystem triggered");
//        JobComponent jobComponent = Mappers.jobs.get(workshopEntity);

        SettlementComponent settlement = getSettlementFromAsset(workshopEntity);
        BuildingComponent building = Mappers.building.get(workshopEntity);
        WorkshopComponent workshop = Mappers.workshop.get(workshopEntity);


//        StorageComponent workshopStorageComponent = workshopEntity.getComponent(StorageComponent.class);
//        Map<String, StorageItem> requiredResources = itemService.getStorageService().getRequiredResources(workshopStorageComponent);
//
//        // find a harvestable item and add it
//        // specifically a tree for the wood cutter
//
//        if (settlement != null) {
//            if (!WorkOrderHelper.doesOrderAlreadyExist(workshopEntity)) {
//                IWorkOrder workOrder = new WorkOrder(workshopEntity, harvestableEntity, itemService.getStorageItem(workshopEntity), HARVEST);
//                settlement.addWorkOrder(workOrder);
//            }
//        }
    }
}
