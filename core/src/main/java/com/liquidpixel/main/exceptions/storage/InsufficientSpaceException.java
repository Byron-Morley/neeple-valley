package com.liquidpixel.main.exceptions.storage;

public class InsufficientSpaceException extends StorageException {

    final String itemName;
    final int quantityToAdd;

    public InsufficientSpaceException(int quantityToAdd, String itemName) {
        super("Not enough space in storage for " + quantityToAdd + " of " + itemName + ". Available space: ");
        this.itemName = itemName;
        this.quantityToAdd = quantityToAdd;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantityToAdd() {
        return quantityToAdd;
    }
}
