package com.liquidpixel.main.systems.settlement;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.liquidpixel.main.components.AssetComponent;
import com.liquidpixel.main.components.agent.AgentComponent;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.components.storage.ProviderComponent;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.exceptions.storage.InsufficientQuantityException;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.interfaces.work.IWorkOrder;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.main.model.storage.WorkOrder;
import com.liquidpixel.main.results.StorageQueryResult;
import com.liquidpixel.main.services.items.StorageHelper;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.main.utils.predicates.Sorter;
import com.liquidpixel.main.utils.predicates.Storage;

import java.util.Comparator;
import java.util.List;

import static com.liquidpixel.main.model.Work.TRANSPORT;
import static com.liquidpixel.main.utils.utils.getSettlementFromAsset;

public class ProviderWorkCreationSystem extends IntervalIteratingSystem {

    public ProviderWorkCreationSystem() {
        super(Family.all(StorageComponent.class, AssetComponent.class, ProviderComponent.class).exclude(AgentComponent.class).get(), 2f);
    }


    // checks that this storage has anything in that needs hauling away from it
    @Override
    protected void processEntity(Entity entity) {
        SettlementComponent settlement = getSettlementFromAsset(entity);
        StorageComponent storage = Mappers.storage.get(entity);

        int priority = storage.getPriority();
        IStorageItem item = StorageHelper.getAnyItemICanCarry(storage);

        if (item != null && item.getQuantity() > 0) {

            List<IWorkOrder> workOrders = settlement.getWorkOrders();
            int totalExistingWorkQuantity = getTotalExistingWorkQuantityFromOrigin(entity, workOrders, item);

            int quantity = item.getQuantity() + totalExistingWorkQuantity;

            try {
                //check if the origin can supply it, includes existing work orders from origin
                if (StorageHelper.isItemQuantityAvailable(storage, new StorageItem(item.getName(), quantity, item.getStackSize(), item.getSprite()))) {

                    //THIS is the hauling one
                    List<Entity> storageLocations = findStorageLocations(entity, item, priority);

                    for (Entity storageLocation : storageLocations) {
                        if (item.getQuantity() <= 0) return;
                        List<IWorkOrder> storageWorkOrders = getDestinationWorkOrders(storageLocation, workOrders);
                        StorageQueryResult result = StorageHelper.isThereEnoughSpaceForItem(storageLocation, item, storageWorkOrders);

                        if (result.getSpaceAvailable() > 0) {
                            int spaceAvailable = Math.min(result.getSpaceAvailable(), item.getQuantity());
                            IWorkOrder workOrder = new WorkOrder(
                                entity,
                                storageLocation,
                                new StorageItem(item.getName(), spaceAvailable, item.getStackSize(), item.getSprite()),
                                TRANSPORT
                            );
                            settlement.addWorkOrder(workOrder);
                            item.setQuantity(item.getQuantity() - spaceAvailable);
                        }
                    }
                }
            } catch (InsufficientQuantityException exception) {
//                System.out.println(exception.getMessage());
            }
        }
    }

    private static List<IWorkOrder> getDestinationWorkOrders(Entity destination, List<IWorkOrder> workOrders) {
        return workOrders
            .stream()
            .filter(work -> work.getDestination().equals(destination))
            .filter(IWorkOrder::isActive)
            .filter(work -> work.getType().equals(TRANSPORT))
            .toList();
    }

    private static int getTotalExistingWorkQuantityFromOrigin(Entity origin, List<IWorkOrder> workOrders, IStorageItem item) {
        return workOrders
            .stream()
            .filter(work -> work.getOrigin().equals(origin))
            .filter(IWorkOrder::isActive)
            .filter(work -> work.getItemName().equals(item.getName()))
            .mapToInt(IWorkOrder::getQuantity)
            .sum();
    }

    private List<Entity> findStorageLocations(Entity entity, IStorageItem item, int priority) {
        Sorter sorter = new Sorter(Mappers.position.get(entity).getPosition());
        Storage storage = new Storage(item);
        SettlementComponent settlement = getSettlementFromAsset(entity);

        return settlement.getAssets()
            .stream()
            .filter(Mappers.storage::has)
            .filter(storageLocation -> Mappers.storage.get(storageLocation).getPriority() > priority)
            .filter(storageLocation -> !Mappers.consumer.has(storageLocation))
            .filter(storage::hasAnySpace)
            .sorted(Comparator.comparing(
                (Entity e) -> Mappers.storage.get(e).getPriority(),
                Comparator.reverseOrder()  // Higher priority first
            ).thenComparing(sorter::sortByCloseness))
            .toList();

    }
}
