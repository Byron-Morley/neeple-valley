package com.liquidpixel.item.components;

import com.badlogic.ashley.core.Component;
import com.liquidpixel.main.factories.ModelFactory;
import com.liquidpixel.main.model.item.Item;

import static com.liquidpixel.main.utils.utils.getFilenameFromPath;

public class ItemComponent implements Component {

    private final String name;
    private int quantity;
    private final String label;
    private boolean isStackable;
    private int stackSize;
    private int unitSize;

    public ItemComponent(
        String name,
        int quantity
    ) {
        this.name = name;
        this.quantity = quantity;

        Item item = ModelFactory.getItemsModel().get(name);
        try {
            if (item == null) {
                throw new IllegalArgumentException("Item not found: " + name);
            }
        } catch (Exception e) {
            System.err.println("Error creating ItemComponent with name: " + name);
            e.printStackTrace();
        }

        this.label = item.getLabel();
        this.isStackable = item.isStackable();
        this.stackSize = item.getStackSize();
        this.unitSize = item.getUnitSize();
    }

    public String getName() {
        return name;
    }

    public int getStackSize() {
        return stackSize;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getLabel() {
        return label;
    }

    public boolean isStackable() {
        return isStackable;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public int getUnitSize() {
        return unitSize;
    }

    public String getRecipeName() {
        return getFilenameFromPath(name);
    }
}
