package com.liquidpixel.main.exceptions.storage;

public class NoSlotsAvailableException extends StorageException {
    private final int totalSlots;

    public NoSlotsAvailableException(int totalSlots) {
        super("Not enough slots in storage. All " + totalSlots + " slots are occupied.");
        this.totalSlots = totalSlots;
    }

    public int getTotalSlots() {
        return totalSlots;
    }
}
