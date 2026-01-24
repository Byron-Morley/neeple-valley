package com.liquidpixel.main.utils;

import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.model.item.StorageItem;

public class Storage {

    //Removes the quantity from the Storage Item, regardless of the quantity in the Storage Item.
    //Original StorageITem can have 0 in it and still exist
    public static IStorageItem splitItem(IStorageItem item, int quantity) {

        int total = Math.min(quantity, item.getQuantity());
        item.setQuantity(item.getQuantity() - total);

        return new StorageItem(item.getName(), total, item.getStackSize(), item.getSprite());
    }

    public static IStorageItem mergeItems(IStorageItem target, IStorageItem source, boolean destroySource) {
        if (target.getName().equals(source.getName())) {
            target.setQuantity(target.getQuantity() + source.getQuantity());
            if (destroySource) {
                source = null;
            } else {
                source.setQuantity(0);
            }
            return target;
        }
        return target;
    }

    public static IStorageItem mergeItems(IStorageItem target, IStorageItem source) {
        return mergeItems(target, source, true);
    }

}
