package com.liquidpixel.main.exceptions.storage;

public class InsufficientQuantityException extends StorageException {
    private final String itemName;
    private final int available;
    private final int requested;

    public InsufficientQuantityException(String itemName, int available, int requested) {
        String message = "Not enough quantity of item " + itemName + ". Available: " + available + ", Requested: " + requested;
        this.itemName = itemName;
        this.available = available;
        this.requested = requested;
    }

    public String getItemName() {
        return itemName;
    }

    public int getAvailable() {
        return available;
    }

    public int getRequested() {
        return requested;
    }
}
