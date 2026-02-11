package com.liquidpixel.main.systems.work;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.liquidpixel.main.components.agent.WorkerComponent;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.handlers.HarvestWorkHandler;
import com.liquidpixel.main.handlers.TransportWorkHandler;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.main.interfaces.work.IWorkOrder;
import com.liquidpixel.main.interfaces.work.IWorkOrderHandler;
import com.liquidpixel.main.model.storage.WorkOrder;
import com.liquidpixel.main.utils.Mappers;

import java.util.Iterator;
import java.util.List;

public class WorkProcessingSystem extends IteratingSystem {

    IMapService mapService;
    IItemService itemService;

    private final TransportWorkHandler transportHandler;
    private final HarvestWorkHandler harvestHandler;


    public WorkProcessingSystem(IMapService mapService, IItemService itemService) {
        super(Family.all(SettlementComponent.class).get());
        this.mapService = mapService;
        this.itemService = itemService;

        // Initialize handlers
        this.harvestHandler = new HarvestWorkHandler(mapService, itemService);
        this.transportHandler = new TransportWorkHandler(mapService, itemService);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SettlementComponent settlement = Mappers.settlement.get(entity);
        List<IWorkOrder> workOrderList = settlement.getWorkOrders();

        Iterator<IWorkOrder> iterator = workOrderList.iterator();
        while (iterator.hasNext()) {
            IWorkOrder workOrder = iterator.next();
            if (workOrder.getState().equals(WorkOrder.State.RUNNING)) {
                if (workOrder.isComplete()) {
                    completeWork(workOrder, iterator);
                }
            }
        }
    }

    private IWorkOrderHandler getHandlerForWorkType(IWorkOrder workOrder) {
        switch (workOrder.getType()) {
            case TRANSPORT:
                return transportHandler;
            case HARVEST:
                return harvestHandler;
            default:
                throw new IllegalArgumentException("Unsupported work type: " + workOrder.getType());
        }
    }


    private void completeWork(IWorkOrder workOrder, Iterator iterator) {
        workOrder.setState(WorkOrder.State.COMPLETED);

        IWorkOrderHandler handler = getHandlerForWorkType(workOrder);
        handler.completeWork(workOrder);
//        //move the goods from origin to destination
//        StorageComponent originStorage = Mappers.storage.get(workOrder.getOrigin());
//        StorageComponent destinationStorage = Mappers.storage.get(workOrder.getDestination());
//
//        IStorageItem item = workOrder.getItem();
//        try {
//            originStorage.unReserveItem(new StorageItem(item.getName(), item.getQuantity(), item.getStackSize(), item.getSprite()));
//            storageService.removeItem(originStorage, new StorageItem(item.getName(), item.getQuantity(), item.getStackSize(), item.getSprite()));
//
//            destinationStorage.unReserveSpace(new StorageItem(item.getName(), item.getQuantity(), item.getStackSize(), item.getSprite()));
//            storageService.addItem(destinationStorage, new StorageItem(item.getName(), item.getQuantity(), item.getStackSize(), item.getSprite()));
//        } catch (Exception exception) {
//            System.out.println(exception.getMessage());
//        }
//
        Entity worker = workOrder.getWorker();
        if (worker != null && Mappers.agent.has(worker)) {
            Mappers.worker.get(worker).state = WorkerComponent.State.IDLE;
        }
//
//        if (originStorage.isOneUse()) {
//            ResourceComponent resourceComponent = Mappers.resource.get(workOrder.getOrigin());
//            if (resourceComponent.isReserved()) GameResources.get().getEngine().removeEntity(resourceComponent.getReserved());
//            GameResources.get().getEngine().removeEntity(workOrder.getOrigin());
//
//        }

//        iterator.remove();
    }
}
