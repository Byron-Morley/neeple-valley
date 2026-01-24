package com.liquidpixel.main.handlers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.TileSelectionComponent;
import com.liquidpixel.main.components.ai.actions.HaulComponent;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.main.interfaces.services.IStorageService;
import com.liquidpixel.main.interfaces.work.IWorkOrder;
import com.liquidpixel.main.interfaces.work.IWorkOrderHandler;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.main.model.storage.WorkOrder;
import com.liquidpixel.main.results.WorkOrderResult;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.main.utils.predicates.Sorter;
import com.liquidpixel.main.utils.predicates.Storage;

import java.util.ArrayList;
import java.util.List;

import static com.liquidpixel.main.screens.WorldScreen.STACK_TO_CARRY_RATIO;

public class TransportWorkHandler implements IWorkOrderHandler {
    private final IStorageService storageService;
    private final IMapService mapService;
    private final IItemService itemService;
    private final IWorldMap worldMap;

    public TransportWorkHandler(IStorageService storageService, IMapService mapService, IItemService itemService) {
        this.storageService = storageService;
        this.mapService = mapService;
        this.itemService = itemService;
        this.worldMap = mapService.getWorldMap();
    }

    @Override
    public WorkOrderResult process(IWorkOrder workOrder, Entity worker, SettlementComponent settlement) {

        // Calculate worker's carry capacity
        int itemStackSize = workOrder.getItem().getStackSize();
        int workerCarryCapacity = calculateWorkerCarryCapacity(itemStackSize, settlement.getWorkerStackCarryLimit());

        // Check if worker can carry the entire quantity
        if (workOrder.getQuantity() <= workerCarryCapacity) {
            return processFullWorkOrder(workOrder, worker);
        } else {
            return processSplitWorkOrder(workOrder, worker, workerCarryCapacity);
        }
    }


    private WorkOrderResult processFullWorkOrder(IWorkOrder workOrder, Entity worker) {
        if (storageService.isWorkValid(workOrder)) {
            assignTransportWorkOrder(workOrder, worker);
            return WorkOrderResult.success();
        } else {
            return WorkOrderResult.remove();
        }
    }

    private WorkOrderResult processSplitWorkOrder(IWorkOrder workOrder, Entity worker, int workerCarryCapacity) {
        int quantityToProcess = workerCarryCapacity;
        WorkOrder splitWorkOrder = createSplitWorkOrder(workOrder, quantityToProcess);

        if (storageService.isWorkValid(splitWorkOrder)) {
            // Update original work order quantity
            workOrder.setQuantity(workOrder.getQuantity() - quantityToProcess);

            // Assign the split work order
            assignTransportWorkOrder(splitWorkOrder, worker);

            List<WorkOrder> newWorkOrders = new ArrayList<>();
            newWorkOrders.add(splitWorkOrder);

            return WorkOrderResult.withNewWorkOrders(newWorkOrders);
        } else {
            return WorkOrderResult.remove();
        }
    }

    private GridPoint2 calculateDropLocation(Entity agent, Entity origin, Entity destination, IStorageItem item) {
        GridPoint2 agentPosition = Mappers.position.get(agent).getGridPosition();
        TileSelectionComponent tileSelection = Mappers.tileSelection.get(destination);

        List<GridPoint2> potentialDropPositions = tileSelection.getAbsolutePoints(Mappers.position.get(destination).getGridPosition());
        Sorter sorter = new Sorter(Mappers.position.get(origin).getPosition());
        Storage storageSorter = new Storage(item, worldMap);

        List<GridPoint2> existingStacks = potentialDropPositions
            .stream()
            .filter(storageSorter::filterByIncompleteStacks)
            .sorted(sorter::sortByCloseness)
            .toList();

        for (GridPoint2 position : existingStacks) {
            //check if you can reach the position
            GridPoint2 potentialTarget = mapService.findValidInteractionPoint(agentPosition, position);
            if (potentialTarget != null) {
                return position;
            }
        }

        List<GridPoint2> emptyStacks = potentialDropPositions
            .stream()
            .filter(storageSorter::filterByEmptyStacks)
            .sorted(sorter::sortByCloseness)
            .toList();

        for (GridPoint2 position : emptyStacks) {
            //check if you can reach the position
            GridPoint2 potentialTarget = mapService.findValidInteractionPoint(agentPosition, position);
            if (potentialTarget != null) {
                return position;
            }
        }

        return null;
    }

    private void assignTransportWorkOrder(IWorkOrder workOrder, Entity worker) {
        // Reserve the item at origin and space at destination
        StorageComponent originStorage = Mappers.storage.get(workOrder.getOrigin());
        StorageComponent destinationStorage = Mappers.storage.get(workOrder.getDestination());

        originStorage.reserveItem(workOrder.getItem());
        destinationStorage.reserveSpace(workOrder.getItem());

        GridPoint2 dropLocation = null;
        if (destinationStorage.isGroundSpace()) {
            dropLocation = calculateDropLocation(worker,  workOrder.getOrigin(), workOrder.getDestination(), workOrder.getItem());
        }

        if (dropLocation == null) {
            dropLocation = Mappers.position.get(workOrder.getDestination()).getGridPosition();
        }

        // Create a haul component and add it to the worker
        HaulComponent haulComponent = new HaulComponent(
            workOrder.getItem(),
            Mappers.position.get(workOrder.getOrigin()).getGridPosition(),
            dropLocation,
            workOrder.getDestination(),
            workOrder.getOrigin()
        );

        worker.add(haulComponent);
        workOrder.setComponent(haulComponent);
        workOrder.start(worker);

        // Mark worker as busy
        Mappers.worker.get(worker).setBusy(true);
    }

    private WorkOrder createSplitWorkOrder(IWorkOrder original, int quantity) {
        return new WorkOrder(
            original.getOrigin(),
            original.getDestination(),
            new StorageItem(
                original.getItemName(),
                quantity,
                original.getItem().getStackSize(),
                original.getItem().getSprite()
            ),
            original.getType()
        );
    }

    private int calculateWorkerCarryCapacity(int itemStackSize, float workerStackCarryLimit) {
        return (int) ((itemStackSize / STACK_TO_CARRY_RATIO) * workerStackCarryLimit);
    }

    @Override
    public void completeWork(IWorkOrder workOrder) {
        StorageComponent originStorage = Mappers.storage.get(workOrder.getOrigin());
        StorageComponent destinationStorage = Mappers.storage.get(workOrder.getDestination());
//This should of been done already
//        if (originStorage != null) {
//            originStorage.unReserveItem(workOrder.getItem());
//        }
        destinationStorage.unReserveSpace(workOrder.getItem());
    }
}
