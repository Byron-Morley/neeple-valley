package com.liquidpixel.main.model.item;

public class StorageItemDTO {

    int quantity;
    String name;

    public StorageItemDTO(int quantity, String name) {
        this.quantity = quantity;
        this.name = name;
    }

    public StorageItemDTO() {
    }

    public int getQuantity() {
        return quantity;
    }
    public String getName() {
        return name;
    }
}
