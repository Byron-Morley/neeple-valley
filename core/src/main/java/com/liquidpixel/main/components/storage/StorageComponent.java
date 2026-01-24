package com.liquidpixel.main.components.storage;

import com.badlogic.ashley.core.Component;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.model.item.Storage;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.sprite.model.GameSprite;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StorageComponent implements Component {

    int priority = 0;

    boolean isActive = false;

    boolean oneUse = false;

    boolean isGroundSpace = false;

    int slots;
    Map<String, IStorageItem> items;
    Map<String, IStorageItem> reservedItems;
    Map<String, IStorageItem> reservedSpace;

    public StorageComponent(int slots) {
        this.slots = slots;
        items = new HashMap<>();
        reservedItems = new HashMap<>();
        reservedSpace = new HashMap<>();
    }

    public StorageComponent(Storage storage) {
        this.slots = storage.getSlots();
        this.isGroundSpace = storage.isGroundSpace();
        this.priority = storage.getPriority();
        items = new HashMap<>();
        reservedItems = new HashMap<>();
        reservedSpace = new HashMap<>();
    }

    public int getSlots() {
        return slots;
    }

    public Map<String, IStorageItem> getItems() {
        return items;
    }

    public IStorageItem getFirstItem() {
        if (!items.isEmpty()) {
            Map.Entry<String, IStorageItem> firstEntry = items.entrySet().iterator().next();
            String firstKey = firstEntry.getKey();
            IStorageItem item = firstEntry.getValue();

            String itemName = item.getName();
            int quantity = item.getQuantity();
            int itemStackSize = item.getStackSize();
            GameSprite sprite = item.getSprite();

            return new StorageItem(itemName, quantity, itemStackSize, sprite);
        }
        return null;
    }

    public Map<String, IStorageItem> getReservedItems() {
        return reservedItems;
    }

    public Map<String, IStorageItem> getReservedSpace() {
        return reservedSpace;
    }

    public IStorageItem removeItem(String itemName) {
        return items.remove(itemName);
    }

    public void reserveItem(IStorageItem item) {
        if (item == null) return;

        String itemName = item.getName();
        int quantityToReserve = item.getQuantity();
        int itemStackSize = item.getStackSize();
        GameSprite sprite = item.getSprite();

        if (reservedItems.get(itemName) == null) {
            // Create a new instance instead of using the passed item directly
            reservedItems.put(itemName, new StorageItem(itemName, quantityToReserve, itemStackSize, sprite));
        } else {
            IStorageItem existingItem = reservedItems.get(itemName);
            // Create a new item with updated quantity
            IStorageItem updatedItem = new StorageItem(itemName,
                existingItem.getQuantity() + quantityToReserve, itemStackSize, sprite);
            reservedItems.put(itemName, updatedItem);
        }
    }

    public void unReserveItem(IStorageItem item) {
        if (item == null) return;

        String itemName = item.getName();
        int quantityToUnreserve = item.getQuantity();
        int itemStackSize = item.getStackSize();
        GameSprite sprite = item.getSprite();

        IStorageItem existingItem = reservedItems.get(itemName);
        if (existingItem == null) return;

        if (existingItem.getQuantity() > quantityToUnreserve) {
            // Create a new item with reduced quantity
            IStorageItem updatedItem = new StorageItem(itemName,
                existingItem.getQuantity() - quantityToUnreserve, itemStackSize, sprite);
            reservedItems.put(itemName, updatedItem);
        } else {
            // Remove the item completely if all quantity is unreserved
            reservedItems.remove(itemName);
        }
    }

    public void reserveSpace(IStorageItem item) {
        if (item == null) return;

        String itemName = item.getName();
        int quantityToReserve = item.getQuantity();
        int itemStackSize = item.getStackSize();
        GameSprite sprite = item.getSprite();

        if (reservedSpace.get(itemName) == null) {
            // Create a new instance instead of using the passed item directly
            reservedSpace.put(itemName, new StorageItem(itemName, quantityToReserve, itemStackSize, sprite));
        } else {
            IStorageItem existingItem = reservedSpace.get(itemName);
            // Create a new item with updated quantity
            IStorageItem updatedItem = new StorageItem(itemName,
                existingItem.getQuantity() + quantityToReserve, itemStackSize, sprite);
            reservedSpace.put(itemName, updatedItem);
        }
    }

    public void unReserveSpace(IStorageItem item) {
        if (item == null) return;

        String itemName = item.getName();
        int quantityToUnreserve = item.getQuantity();
        int itemStackSize = item.getStackSize();
        GameSprite sprite = item.getSprite();

        IStorageItem existingItem = reservedSpace.get(itemName);
        if (existingItem == null) return;

        if (existingItem.getQuantity() > quantityToUnreserve) {
            // Create a new item with reduced quantity
            IStorageItem updatedItem = new StorageItem(itemName,
                existingItem.getQuantity() - quantityToUnreserve, itemStackSize, sprite);
            reservedSpace.put(itemName, updatedItem);
        } else {
            // Remove the item completely if all space is unreserved
            reservedSpace.remove(itemName);
        }
    }

    public List<IStorageItem> calculateItemStacks() {
        List<IStorageItem> itemStacks = new ArrayList<>();

        for (Map.Entry<String, IStorageItem> entry : items.entrySet()) {
            IStorageItem item = entry.getValue();

            String itemName = item.getName();
            int quantity = item.getQuantity();
            int stackSize = item.getStackSize();
            GameSprite sprite = item.getSprite();

            int fullStacks = quantity / stackSize;
            int remainder = quantity % stackSize;

            for (int i = 0; i < fullStacks; i++) {
                IStorageItem stackItem = new StorageItem(itemName, stackSize, stackSize, sprite);
                itemStacks.add(stackItem);
            }

            if (remainder > 0) {
                IStorageItem remainderItem = new StorageItem(itemName, remainder, stackSize, sprite);
                itemStacks.add(remainderItem);
            }
        }

        return itemStacks;
    }


    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void addItem(IStorageItem storageItem) {
        items.put(storageItem.getName(), storageItem);
    }

    public boolean isOneUse() {
        return oneUse;
    }

    public void setOneUse(boolean oneUse) {
        this.oneUse = oneUse;
    }

    public void addItems(List<IStorageItem> list) {
        for (IStorageItem item : list) {
            addItem(item);
        }
    }

    public boolean isGroundSpace() {
        return isGroundSpace;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
