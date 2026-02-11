package com.liquidpixel.main.services.items;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.liquidpixel.main.components.AreaSpawnConfigurationComponent;
import com.liquidpixel.main.components.StorageGroupComponent;
import com.liquidpixel.main.components.TileSelectionComponent;
import com.liquidpixel.main.components.render.RenderComponent;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.exceptions.storage.InsufficientQuantityException;
import com.liquidpixel.main.exceptions.storage.InsufficientSpaceException;
import com.liquidpixel.main.interfaces.*;
import com.liquidpixel.main.interfaces.services.ISettlementService;
import com.liquidpixel.main.interfaces.work.IWorkOrder;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.sprite.model.GameSprite;
import com.liquidpixel.main.results.StorageQueryResult;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageHelper {

    public static String RESOURCE_STORAGE = "storage/invisible_storage";

    public static void addItem(Entity storage, IStorageItem storageItem) {
        if (Mappers.storage.has(storage)) {
            StorageComponent storageComponent = Mappers.storage.get(storage);
            addItem(storageComponent, storageItem);
        } else {
            Gdx.app.error("StorageHelper", "Storage entity does not have a storage component");
        }
    }

    public static void addItem(StorageComponent storage, IStorageItem storageItem) {
        if (storageItem == null || storageItem.getQuantity() <= 0) {
            return;
        }

        String itemName = storageItem.getName();
        int quantityToAdd = storageItem.getQuantity();

        // Check if we have enough space first
        if (!hasSpace(storage, storageItem)) {
            throw new InsufficientSpaceException(quantityToAdd, itemName);
        }

        // Check if item already exists in storage
        IStorageItem item = storage.getItems().get(itemName);

        if (item != null) {
            IStorageItem updatedItem = new StorageItem(itemName, item.getQuantity() + quantityToAdd, item.getStackSize(), item.getSprite());
            storage.getItems().put(itemName, updatedItem);
        } else {
            // Item doesn't exist, create a new one
            IStorageItem newItem = new StorageItem(itemName, quantityToAdd, storageItem.getStackSize(), storageItem.getSprite());
            storage.getItems().put(itemName, newItem);
        }
    }


    public static IStorageItem removeItem(StorageComponent storage, IStorageItem storageItem) {
        if (storageItem.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity to remove must be positive");
        }

        String itemName = storageItem.getName();
        int quantity = storageItem.getQuantity();
        int stackSize = storageItem.getStackSize();
        GameSprite sprite = storageItem.getSprite();


        // Get the item from storage
        IStorageItem item = storage.getItems().get(itemName);
        if (item == null) {
            throw new IllegalArgumentException("Item " + itemName + " doesn't exist in storage");
        }

        // Check if sufficient unreserved quantity is available
        if (isItemQuantityAvailable(storage, storageItem)) {
            // Create the return item
            IStorageItem removedItem = new StorageItem(itemName, quantity, stackSize, sprite);

            // Update the quantity in storage
            int newQuantity = item.getQuantity() - quantity;
            if (newQuantity > 0) {
                item.setQuantity(newQuantity);
            } else {
                storage.removeItem(itemName);
            }

            return removedItem;
        }
        return null;
    }


    public static int getAvailableQuantity(StorageComponent storage, String itemName) {
        try {

            // Get the item from storage
            IStorageItem item = storage.getItems().get(itemName);
            if (item == null) {
                return 0;
            }

            // Get reserved quantity if any
            IStorageItem reservedItem = storage.getReservedItems().get(itemName);
            int reservedQuantity = reservedItem != null ? reservedItem.getQuantity() : 0;

            // Calculate available quantity
            return item.getQuantity() - reservedQuantity;
        } catch (Exception e) {
            return 0;
        }
    }


    public static boolean isItemQuantityAvailable(StorageComponent storage, IStorageItem storageItem) {
        try {
            if (storageItem.getQuantity() <= 0) {
                return false;
            }

            String itemName = storageItem.getName();
            int quantity = storageItem.getQuantity();


            // Get the item from storage
            IStorageItem item = storage.getItems().get(itemName);
            if (item == null) {
                return false;
            }

            // Get reserved quantity if any
            IStorageItem reservedItem = storage.getReservedItems().get(itemName);
            int reservedQuantity = reservedItem != null ? reservedItem.getQuantity() : 0;

            // Calculate available quantity
            int availableQuantity = item.getQuantity() - reservedQuantity;

            // Check if available quantity is sufficient
            if (availableQuantity >= quantity) {
                return true;
            } else {
                throw new InsufficientQuantityException(itemName, availableQuantity, quantity);
            }
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static boolean isWorkValid(IWorkOrder workOrder) {
        try {
            return (isItemQuantityAvailable(Mappers.storage.get(workOrder.getOrigin()), workOrder.getItem())
                && hasSpace(Mappers.storage.get(workOrder.getDestination()), workOrder.getItem())
            );
        } catch (InsufficientQuantityException e) {
            return false;
        }
    }

    public static IStorageItem getAnyItemICanCarry(StorageComponent storage) {
        if (storage == null || storage.getItems().isEmpty()) {
            return null;
        }

        for (Map.Entry<String, IStorageItem> entry : storage.getItems().entrySet()) {
            String itemName = entry.getKey();
            IStorageItem item = entry.getValue();

            // Get reserved quantity if any
            IStorageItem reservedItem = storage.getReservedItems().get(itemName);
            int reservedQuantity = reservedItem != null ? reservedItem.getQuantity() : 0;

            // Calculate available quantity
            int availableQuantity = item.getQuantity() - reservedQuantity;

            if (storage.isOneUse()) {
                return new StorageItem(itemName, availableQuantity, item.getStackSize(), item.getSprite());
            }

            // Check if available quantity is at least a full stack
            if (availableQuantity >= item.getStackSize()) {
                // Return a new StorageItem with all available quantity
                return new StorageItem(itemName, availableQuantity, item.getStackSize(), item.getSprite());
            }
        }
        return null;
    }

    public static IStorageItem getFullStack(StorageComponent storage) {
        if (storage == null || storage.getItems().isEmpty()) {
            return null;
        }

        for (Map.Entry<String, IStorageItem> entry : storage.getItems().entrySet()) {
            String itemName = entry.getKey();
            IStorageItem item = entry.getValue();

            // Get reserved quantity if any
            IStorageItem reservedItem = storage.getReservedItems().get(itemName);
            int reservedQuantity = reservedItem != null ? reservedItem.getQuantity() : 0;

            // Calculate available quantity
            int availableQuantity = item.getQuantity() - reservedQuantity;

            // Check if available quantity is at least a full stack
            if (availableQuantity >= item.getStackSize()) {
                // Return a new StorageItem with a full stack quantity
                return new StorageItem(itemName, item.getStackSize(), item.getStackSize(), item.getSprite());
            }
        }
        return null;
    }


    public static boolean hasSpace(StorageComponent storage, IStorageItem storageItem) {
        if (storageItem.getQuantity() <= 0) {
            return true; // No space needed for zero or negative quantity
        }

        if (storage == null) {
            return false;
        }

        // Get merged map of items and reserved space
        Map<String, IStorageItem> mergedItems = mergeItems(storage.getItems(), storage.getReservedSpace());

        // Check if the item already exists in the merged map
        IStorageItem existingItem = mergedItems.get(storageItem.getName());

        int requiredSlots = 0;
        int stackSize = storageItem.getStackSize();

        if (existingItem != null) {
            // Calculate how much of the quantity can fit in the current incomplete stack
            int currentQuantity = existingItem.getQuantity();
            int remainingSpaceInCurrentStack = stackSize - (currentQuantity % stackSize);

            // If current stack is full, remainingSpaceInCurrentStack will be stackSize
            if (remainingSpaceInCurrentStack == stackSize) {
                remainingSpaceInCurrentStack = 0;
            }

            // If some quantity can fit in current stack, reduce the quantity that needs new slots
            int quantityNeedingNewSlots = storageItem.getQuantity() - remainingSpaceInCurrentStack;

            if (quantityNeedingNewSlots > 0) {
                // Calculate additional slots needed
                requiredSlots = (int) Math.ceil((double) quantityNeedingNewSlots / stackSize);
            }
        } else {
            // This is a new item type, calculate slots based on quantity and stack size
            requiredSlots = (int) Math.ceil((double) storageItem.getQuantity() / stackSize);
        }

        // Check if we have enough available slots
        int availableSlots = getAvailableSlots(storage);
        return availableSlots >= requiredSlots;
    }


    public static boolean hasAnySpace(StorageComponent storage, IStorageItem storageItem) {
        if (storageItem == null) {
            return false;
        }

        // Create a copy of the item with quantity of 1
        IStorageItem singleItem = new StorageItem(
            storageItem.getName(),
            1,  // We only want to check for one unit
            storageItem.getStackSize(),
            storageItem.getSprite()
        );

        // Use the existing hasSpace method with the modified item
        return hasSpace(storage, singleItem);
    }

    public static int getAvailableSlots(StorageComponent storage) {
        int totalSlots = storage.getSlots();

        // Merge regular items with reserved space to get an accurate picture of used space
        Map<String, IStorageItem> mergedItems = mergeItems(storage.getItems(), storage.getReservedSpace());

        int usedSlots = 0;

        // Calculate how many slots are used by existing items including reserved space
        for (IStorageItem item : mergedItems.values()) {
            int quantity = item.getQuantity();
            int stackSize = item.getStackSize();

            // Calculate how many slots this item takes up
            // Each full stack takes one slot, plus potentially one more for a partial stack
            int slotsForItem = (int) Math.ceil((double) quantity / stackSize);
            usedSlots += slotsForItem;
        }

        return Math.max(0, totalSlots - usedSlots);
    }

    // Add this mergeItems function to support the above methods
    public static Map<String, IStorageItem> mergeItems(Map<String, IStorageItem> targetItems, Map<String, IStorageItem> sourceItems) {
        if (sourceItems == null || sourceItems.isEmpty()) {
            return new HashMap<>(targetItems != null ? targetItems : new HashMap<>());
        }

        if (targetItems == null) {
            targetItems = new HashMap<>();
        }

        // Create a defensive copy to avoid modifying the original maps
        Map<String, IStorageItem> result = new HashMap<>(targetItems);

        // Merge source items into target items
        for (Map.Entry<String, IStorageItem> entry : sourceItems.entrySet()) {
            String itemName = entry.getKey();
            IStorageItem sourceItem = entry.getValue();

            if (sourceItem == null || sourceItem.getQuantity() <= 0) {
                continue;
            }

            IStorageItem targetItem = result.get(itemName);

            if (targetItem != null) {
                // Create a new item with the combined quantity
                IStorageItem newItem = new StorageItem(
                    targetItem.getName(),
                    targetItem.getQuantity() + sourceItem.getQuantity(),
                    targetItem.getStackSize(),
                    targetItem.getSprite()
                );
                result.put(itemName, newItem);
            } else {
                // Item doesn't exist in target, create a new one
                IStorageItem newItem = new StorageItem(
                    sourceItem.getName(),
                    sourceItem.getQuantity(),
                    sourceItem.getStackSize(),
                    sourceItem.getSprite()
                );
                result.put(itemName, newItem);
            }
        }

        return result;
    }


    public static StorageQueryResult isThereEnoughSpaceForItem(Entity storageLocation, IStorageItem item, List<IWorkOrder> storageWorkOrders) {
        StorageQueryResult result = new StorageQueryResult();

        // Get the storage component from the entity
        StorageComponent storage = Mappers.storage.get(storageLocation);
        if (storage == null) {
            result.setSpaceAvailable(0);
            return result;
        }

        // If there are no work orders, we can calculate based on current storage state
        if (storageWorkOrders == null || storageWorkOrders.isEmpty()) {
            return calculateAvailableSpaceForItem(storage, item);
        }

        // Create a temporary storage component to simulate the effect of work orders
        StorageComponent simulatedStorage = new StorageComponent(storage.getSlots());

        // Copy all items from the original storage
        for (Map.Entry<String, IStorageItem> entry : storage.getItems().entrySet()) {
            simulatedStorage.getItems().put(entry.getKey(), new StorageItem(
                entry.getValue().getName(),
                entry.getValue().getQuantity(),
                entry.getValue().getStackSize(),
                entry.getValue().getSprite()
            ));
        }

        // Copy all reserved items
        for (Map.Entry<String, IStorageItem> entry : storage.getReservedItems().entrySet()) {
            simulatedStorage.getReservedItems().put(entry.getKey(), new StorageItem(
                entry.getValue().getName(),
                entry.getValue().getQuantity(),
                entry.getValue().getStackSize(),
                entry.getValue().getSprite()
            ));
        }

        // Copy all reserved space
        for (Map.Entry<String, IStorageItem> entry : storage.getReservedSpace().entrySet()) {
            simulatedStorage.getReservedSpace().put(entry.getKey(), new StorageItem(
                entry.getValue().getName(),
                entry.getValue().getQuantity(),
                entry.getValue().getStackSize(),
                entry.getValue().getSprite()
            ));
        }

        // Process each work order that affects this storage location
        for (IWorkOrder workOrder : storageWorkOrders) {
            // Only consider work orders where this storage is the destination
            if (!workOrder.getDestination().equals(storageLocation)) {
                continue;
            }

            // Get the item from the work order
            IStorageItem workOrderItem = workOrder.getItem();
            if (workOrderItem == null || workOrderItem.getQuantity() <= 0) {
                continue;
            }

            // Add the item to our simulated reserved space
            String itemName = workOrderItem.getName();
            IStorageItem existingReserved = simulatedStorage.getReservedSpace().get(itemName);

            if (existingReserved != null) {
                // Update existing reserved space
                IStorageItem updatedReserved = new StorageItem(
                    itemName,
                    existingReserved.getQuantity() + workOrderItem.getQuantity(),
                    workOrderItem.getStackSize(),
                    workOrderItem.getSprite()
                );
                simulatedStorage.getReservedSpace().put(itemName, updatedReserved);
            } else {
                // Add new reserved space
                simulatedStorage.getReservedSpace().put(itemName, new StorageItem(
                    itemName,
                    workOrderItem.getQuantity(),
                    workOrderItem.getStackSize(),
                    workOrderItem.getSprite()
                ));
            }
        }

        // Calculate available space for the item in the simulated storage
        return calculateAvailableSpaceForItem(simulatedStorage, item);
    }

    /**
     * Calculate how much of a specific item can fit in the storage.
     * This includes empty slots and partially filled slots with the same item.
     */
    private static StorageQueryResult calculateAvailableSpaceForItem(StorageComponent storage, IStorageItem item) {
        StorageQueryResult result = new StorageQueryResult();

        if (item == null || item.getQuantity() <= 0) {
            result.setSpaceAvailable(0);
            return result;
        }

        String itemName = item.getName();
        int stackSize = item.getStackSize();

        // Get merged map of items and reserved space to account for all space usage
        Map<String, IStorageItem> mergedItems = mergeItems(storage.getItems(), storage.getReservedSpace());

        // Calculate total available slots in the storage
        int availableSlots = getAvailableSlots(storage);

        // Calculate space in existing partial stacks of the same item
        int spaceInPartialStacks = 0;
        IStorageItem existingItem = mergedItems.get(itemName);

        if (existingItem != null) {
            // Calculate remaining space in the current stacks
            int currentQuantity = existingItem.getQuantity();
            int fullStacks = currentQuantity / stackSize;
            int partialStackQuantity = currentQuantity % stackSize;

            if (partialStackQuantity > 0) {
                // There's a partial stack with some space
                spaceInPartialStacks = stackSize - partialStackQuantity;
            }
        }

        // Calculate space in new empty slots
        int spaceInNewSlots = availableSlots * stackSize;

        // Total available space for this specific item
        int totalAvailableSpace = spaceInPartialStacks + spaceInNewSlots;

        result.setSpaceAvailable(totalAvailableSpace);

        return result;
    }

    public static Entity createGroupStorage(GridPoint2 point, ISettlementService settlementService, int width, int height) {

        Entity entity = settlementService.buildInSettlement("storage/storage_group", point);
        entity.remove(RenderComponent.class);
        entity.remove(AreaSpawnConfigurationComponent.class);
        entity.add(new TileSelectionComponent(width, height));

        TileSelectionComponent tileSelectionComponent = Mappers.tileSelection.get(entity);
        tileSelectionComponent.setCellSize(1f);
        tileSelectionComponent.setColor(new Color(0.1f, 0.0f, 0.0f, 0.3f));
        tileSelectionComponent.setShowInvalidTiles(false);
        StorageGroupComponent storageGroupComponent = Mappers.storageGroup.get(entity);
        StorageComponent groupStorage = Mappers.storage.get(entity);

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                GridPoint2 gridPoint = new GridPoint2(point.x + x, point.y + y);
                Entity storage = settlementService.buildInSettlement(RESOURCE_STORAGE, gridPoint);
                StorageComponent storageComponent = Mappers.storage.get(storage);
                storageComponent.setPriority(groupStorage.getPriority());
                storageGroupComponent.addStorageSpot(gridPoint, storage);
            }
        }
        return entity;
    }

    public static void setPriority(Entity entity, int priority) {
        if (Mappers.storage.has(entity)) {
            StorageComponent storage = Mappers.storage.get(entity);
            storage.setPriority(priority);
        }
        if (Mappers.storageGroup.has(entity)) {
            StorageGroupComponent storageGroup = Mappers.storageGroup.get(entity);
            storageGroup.setPriority(priority);
        }
    }

    public static Image createStorageImage(IStorageItem item, ISpriteFactory spriteFactory) {
        try {
            // Use the sprite from the IStorageItem
            GameSprite sprite = item.getSprite();

            int spriteWidth = sprite.getRegionWidth();
            int spriteHeight = sprite.getRegionHeight();

            // Check if sprite is square - if not, crop to bottom square portion
            if (spriteWidth != spriteHeight && spriteHeight > spriteWidth) {
                // For non-square sprites (like 16x32), crop to bottom 16x16 portion
                TextureRegion croppedRegion = new TextureRegion(
                    sprite.getTexture(),
                    sprite.getRegionX(),
                    sprite.getRegionY() + (spriteHeight - spriteWidth), // Start at bottom square portion
                    spriteWidth, // Width: same as original width
                    spriteWidth  // Height: make it square (same as width)
                );

                Image image = new Image(new TextureRegionDrawable(croppedRegion));
                image.setScaling(com.badlogic.gdx.utils.Scaling.stretch);
                return image;
            } else {
                // For square sprites, use the original sprite
                Image image = new Image(sprite);
                image.setScaling(com.badlogic.gdx.utils.Scaling.fit);
                return image;
            }
        } catch (Exception e) {
            // Fallback to a default texture if item sprite not found
            System.out.println("Could not load sprite for: " + item.getName());
            try {
                if (spriteFactory != null) {
                    TextureRegion texture = spriteFactory.getTextureWithFallback(item.getSpriteName(), -1);
                    Image fallbackImage = new Image(new TextureRegionDrawable(texture));
                    fallbackImage.setScaling(com.badlogic.gdx.utils.Scaling.fit);
                    return fallbackImage;
                }
            } catch (Exception fallbackException) {
                // Return empty image if no fallback available
                return new Image();
            }
        }
        return new Image();
    }


    public static IStorageItem getResource(Entity entity) {
        if (Mappers.storage.has(entity)) {
            StorageComponent storage = Mappers.storage.get(entity);
            if (!storage.getItems().isEmpty()) {
                return storage.getItems().values().iterator().next();
            }
        }
        return null;
    }
}
