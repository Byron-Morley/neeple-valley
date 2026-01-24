package com.liquidpixel.main.model.item;

import com.liquidpixel.main.interfaces.IStorageItem;

import java.util.ArrayList;
import java.util.List;

public class Storage {

    int slots = 1;
    int priority = 0;
    boolean groundSpace = false;
    boolean group = false;
    boolean workshop = false;
    boolean tile = false;

    private List<IStorageItem> items;

    public Storage(int slots) {
        this.slots = slots;
    }

    public Storage() {
        items = new ArrayList<>();
    }

    public int getSlots() {
        return slots;
    }

    public boolean isGroundSpace() {
        return groundSpace;
    }

    public boolean isWorkshop() {
        return workshop;
    }

    public List<IStorageItem> getItems() {
        return items;
    }

    public void setItems(List<IStorageItem> items) {
        this.items = items;
    }

    public void addItem(IStorageItem item) {
        this.items.add(item);
    }

    public boolean isTile() {
        return tile;
    }

    public boolean isGroup() {
        return group;
    }

    public int getPriority() {
        return priority;
    }
}
