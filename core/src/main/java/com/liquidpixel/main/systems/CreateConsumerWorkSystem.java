package com.liquidpixel.main.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.liquidpixel.main.components.CreateConsumerWorkComponent;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.components.storage.ConsumerComponent;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.exceptions.storage.InsufficientQuantityException;
import com.liquidpixel.main.interfaces.IRecipe;
import com.liquidpixel.main.interfaces.services.ISettlementService;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.main.model.storage.WorkOrder;
import com.liquidpixel.main.services.SettlementService;
import com.liquidpixel.main.services.items.StorageHelper;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.main.utils.predicates.Sorter;
import com.liquidpixel.main.utils.predicates.Storage;

import java.util.List;
import java.util.stream.Collectors;

import static com.liquidpixel.main.model.Work.TRANSPORT;
import static com.liquidpixel.main.utils.utils.getSettlementFromAsset;

public class CreateConsumerWorkSystem extends IteratingSystem {
    ISettlementService settlementService;

    public CreateConsumerWorkSystem(ISettlementService settlementService) {
        super(Family.all(CreateConsumerWorkComponent.class).get());
        this.settlementService = settlementService;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ConsumerComponent consumerComponent = Mappers.consumer.get(entity);
        IRecipe recipe = consumerComponent.getRecipe();
        SettlementComponent settlement = getSettlementFromAsset(entity);
        StorageComponent consumerStorage = Mappers.storage.get(entity);

        // Process each input resource required by the recipe
        for (IStorageItem requiredResource : recipe.getInput()) {

            // How much has already been ordered for this resource
            int totalExistingWorkQuantity = settlementService.getQuantityCurrentlyInWorkOrders(
                entity,
                SettlementService.QueryType.DESTINATION,
                requiredResource.getName()
            );

            // Check how much of the resource is already in the consumer's own storage
            int quantityAlreadyInStorage = 0;
            if (consumerStorage != null) {
                try {
                    quantityAlreadyInStorage = StorageHelper.getAvailableQuantity(consumerStorage, requiredResource.getName());
                } catch (Exception e) {
                    // If there's an error getting the quantity, assume 0
                    quantityAlreadyInStorage = 0;
                }
            }

            // How much more we need to order, accounting for what's already in storage
            int remainingQuantityNeeded = requiredResource.getQuantity() - totalExistingWorkQuantity - quantityAlreadyInStorage;

            if (remainingQuantityNeeded <= 0) {
                continue; // Skip if we already have enough resources (in work orders or in storage)
            }

            // Get all potential supply locations sorted by distance
            List<Entity> supplyLocations = getSupplyLocations(entity, settlement, requiredResource);

            // Create work orders from multiple locations if needed
            for (Entity supplyLocation : supplyLocations) {
                if (remainingQuantityNeeded <= 0) {
                    break; // We've created enough work orders
                }

                StorageComponent storage = Mappers.storage.get(supplyLocation);
                try {
                    // Check if the required quantity is available
                    StorageHelper.isItemQuantityAvailable(storage, new StorageItem(requiredResource.getName(), remainingQuantityNeeded, requiredResource.getStackSize(), requiredResource.getSprite()));

                    // If we get here, the full remaining quantity is available at this location
                    WorkOrder workOrder = new WorkOrder(
                        supplyLocation,
                        entity,
                        new StorageItem(requiredResource.getName(), remainingQuantityNeeded, requiredResource.getStackSize(), requiredResource.getSprite()),
                        TRANSPORT
                    );
                    settlement.addWorkOrder(workOrder);

                    // All needed quantity has been assigned
                    remainingQuantityNeeded = 0;

                } catch (InsufficientQuantityException e) {
                    // Not enough at this location, take what's available and continue to next location
                    int availableQuantity = e.getAvailable();

                    if (availableQuantity > 0) {
                        // Create a work order for the available quantity
                        WorkOrder workOrder = new WorkOrder(
                            supplyLocation,
                            entity,
                            new StorageItem(requiredResource.getName(), availableQuantity, requiredResource.getStackSize(), requiredResource.getSprite()),
                            TRANSPORT
                        );
                        settlement.addWorkOrder(workOrder);

                        // Update remaining needed quantity
                        remainingQuantityNeeded -= availableQuantity;
                    }
                } catch (Exception e) {
                    // Handle other exceptions
                    continue;
                }
            }

            // If we couldn't find enough resources, we might want to log this or handle it
            if (remainingQuantityNeeded > 0) {
                // Maybe mark this consumer as waiting for resources
                // Or create a request for production of this resource
            }
        }

        // Remove the component after processing to avoid repeated processing
        // entity.remove(CreateConsumerWorkComponent.class);
    }

    private List<Entity> getSupplyLocations(Entity consumer, SettlementComponent settlement, IStorageItem item) {
        Sorter sorter = new Sorter(Mappers.position.get(consumer).getPosition());
        Storage storage = new Storage(item);

        return settlement.getAssets()
            .stream()
            .filter(Mappers.storage::has)
            .filter(building -> !building.equals(consumer)) // Exclude the consumer itself from supply locations
            .filter(storage::hasAnyQuantityOfItem)
            .sorted(sorter::sortByCloseness)
            .collect(Collectors.toList());
    }
}
