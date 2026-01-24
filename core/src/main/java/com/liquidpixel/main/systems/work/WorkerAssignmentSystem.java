package com.liquidpixel.main.systems.work;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.liquidpixel.main.components.agent.AgentJobComponent;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.handlers.HarvestWorkHandler;
import com.liquidpixel.main.handlers.TransportWorkHandler;
import com.liquidpixel.main.results.WorkOrderResult;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.main.interfaces.services.IStorageService;
import com.liquidpixel.main.interfaces.work.IWorkOrder;
import com.liquidpixel.main.interfaces.work.IWorkOrderHandler;
import com.liquidpixel.main.model.storage.WorkOrder;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.main.utils.predicates.Sorter;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class WorkerAssignmentSystem extends IntervalIteratingSystem {
    IStorageService storageService;
    IMapService mapService;
    IItemService itemService;

    private final TransportWorkHandler transportHandler;
    private final HarvestWorkHandler harvestHandler;

    public WorkerAssignmentSystem(IStorageService storageService, IMapService mapService, IItemService itemService) {
        super(Family.all(SettlementComponent.class).get(), 2f);
        this.storageService = storageService;
        this.mapService = mapService;
        this.itemService = itemService;

        // Initialize handlers
        this.harvestHandler = new HarvestWorkHandler(mapService, itemService);
        this.transportHandler = new TransportWorkHandler(storageService, mapService, itemService);
    }

    @Override
    protected void processEntity(Entity settlementEntity) {
        SettlementComponent settlement = Mappers.settlement.get(settlementEntity);
        List<IWorkOrder> workOrderList = getWorkOrders(settlement);
        List<Entity> workers = getWorkers(settlement);

        if (workOrderList.isEmpty() || workers.isEmpty()) {
            return;
        }

        for (Entity worker : workers) {
            findAnyJob(workOrderList, worker, settlement);
        }


//        for (Entity worker : workers) {
//            if (Mappers.agentjob.has(worker)) {
//                findJobRelatedWork(workOrderList, worker, settlement);
//            } else {
//                findNonJobRelatedWork(workOrderList, worker, settlement);
//            }
//        }
    }

    private void findAnyJob(List<IWorkOrder> workOrderList, Entity worker, SettlementComponent settlement) {
        List<IWorkOrder> jobWorkOrderList = getAnyJobList(workOrderList, worker);
        Iterator<IWorkOrder> iterator = jobWorkOrderList.iterator();

        while (iterator.hasNext()) {
            IWorkOrder workOrder = iterator.next();

            // Use the appropriate handler based on work type
            IWorkOrderHandler handler = getHandlerForWorkType(workOrder);
            WorkOrderResult result = handler.process(workOrder, worker, settlement);

            // Handle the result
            if (result.shouldRemove()) {
                iterator.remove();
            }

            // Add any new work orders
            settlement.getWorkOrders().addAll(result.getNewWorkOrders());

            // If the worker is now busy, stop processing work orders for this worker
            if (Mappers.worker.get(worker).isBusy()) {
                break;
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


    private void findNonJobRelatedWork(List<IWorkOrder> workOrderList, Entity worker, SettlementComponent settlement) {
        List<IWorkOrder> jobWorkOrderList = getNonJobWorkOrderList(workOrderList, worker);
        Iterator<IWorkOrder> iterator = jobWorkOrderList.iterator();

        while (iterator.hasNext()) {
            IWorkOrder workOrder = iterator.next();

            // Use the appropriate handler based on work type
            IWorkOrderHandler handler = getHandlerForWorkType(workOrder);
            WorkOrderResult result = handler.process(workOrder, worker, settlement);

            // Handle the result
            if (result.shouldRemove()) {
                iterator.remove();
            }

            // Add any new work orders
            settlement.getWorkOrders().addAll(result.getNewWorkOrders());

            // If the worker is now busy, stop processing work orders for this worker
            if (Mappers.worker.get(worker).isBusy()) {
                break;
            }
        }
    }

    private void findJobRelatedWork(List<IWorkOrder> workOrderList, Entity worker, SettlementComponent settlement) {
        AgentJobComponent agentJob = Mappers.agentjob.get(worker);
        List<IWorkOrder> jobWorkOrderList = getJobWorkOrderList(workOrderList, agentJob.getJob());
        Iterator<IWorkOrder> iterator = jobWorkOrderList.iterator();

        while (iterator.hasNext()) {
            IWorkOrder workOrder = iterator.next();

            // Use the appropriate handler based on work type
            IWorkOrderHandler handler = getHandlerForWorkType(workOrder);
            WorkOrderResult result = handler.process(workOrder, worker, settlement);

            // Handle the result
            if (result.shouldRemove()) {
                iterator.remove();
            }

            // Add any new work orders
            settlement.getWorkOrders().addAll(result.getNewWorkOrders());

            // If the worker is now busy, stop processing work orders for this worker
            if (Mappers.worker.get(worker).isBusy()) {
                break;
            }
        }
    }

//
//
//    private void assignJobRelatedTransportJob(IWorkOrder workOrder, Entity worker, Iterator<IWorkOrder> iterator, Entity settlementEntity) {
//        AgentJobComponent agentJob = Mappers.agentjob.get(worker);
//        Entity job = agentJob.getJob();
//        if (workOrder.getDestination().equals(job) || workOrder.getOrigin().equals(job)) {
//
//

    /// /            we need to check whether the worker can carry the capacity
    /// /            if they cant we need to be able to split the job and add more to the settlement
//
//
//        }
//    }
//
    private List<Entity> getWorkers(SettlementComponent settlement) {
        return settlement.getPopulation()
            .stream()
            .filter(Mappers.worker::has)
            .filter(worker -> !Mappers.worker.get(worker).isBusy())
            .toList();
    }

    private List<IWorkOrder> getWorkOrders(SettlementComponent settlement) {
        return settlement.getWorkOrders()
            .stream()
            .filter(IWorkOrder::isActive)
            .sorted(this::compareWorkOrdersByType)
            .toList();
    }

    private List<IWorkOrder> getAnyJobList(List<IWorkOrder> workOrderList, Entity worker) {
        Sorter sorter = new Sorter(Mappers.position.get(worker).getPosition());

        return workOrderList
            .stream()
            .filter(workOrder -> workOrder.getState().equals(WorkOrder.State.PENDING))
            .sorted((wo1, wo2) -> sorter.sortByCloseness(wo1.getDestination(), wo2.getDestination()))
            .sorted(this::compareWorkOrdersByPriority)
            .collect(Collectors.toList());
    }

    private List<IWorkOrder> getNonJobWorkOrderList(List<IWorkOrder> workOrderList, Entity worker) {
        Sorter sorter = new Sorter(Mappers.position.get(worker).getPosition());

        return workOrderList
            .stream()
            .filter(workOrder -> workOrder.getState().equals(WorkOrder.State.PENDING))
            .filter(workOrder -> !hasWorkerAttached(workOrder))
            .sorted((wo1, wo2) -> sorter.sortByCloseness(wo1.getDestination(), wo2.getDestination()))
            .sorted(this::compareWorkOrdersByPriority)
            .collect(Collectors.toList());
    }

    private boolean hasWorkerAttached(IWorkOrder workOrder) {
        Entity destination = workOrder.getDestination();

        if (Mappers.jobs.has(destination)) {
            if (!Mappers.jobs.get(destination).getWorkers().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private List<IWorkOrder> getJobWorkOrderList(List<IWorkOrder> workOrderList, Entity agentJobLocation) {
        return workOrderList.stream()
            .filter(workOrder -> workOrder.getState().equals(WorkOrder.State.PENDING))
            .filter(workOrder ->
                workOrder.getOrigin().equals(agentJobLocation)
                    || workOrder.getDestination().equals(agentJobLocation))
            .sorted(this::compareWorkOrdersByPriority)
            .collect(Collectors.toList());
    }

    public int compareWorkOrdersByType(IWorkOrder wo1, IWorkOrder wo2) {
        // Compare by Work enum priority value (lower value = higher priority)
        return Integer.compare(wo1.getType().getValue(), wo2.getType().getValue());
    }

    public int compareWorkOrdersByPriority(IWorkOrder wo1, IWorkOrder wo2) {
        return Integer.compare(wo2.getPriority(), wo1.getPriority());
    }

}
