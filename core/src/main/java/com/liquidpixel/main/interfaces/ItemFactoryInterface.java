package com.liquidpixel.main.interfaces;

import com.liquidpixel.item.builders.ItemBuilder;

public interface ItemFactoryInterface {
    ItemBuilder createItem(String name, int quantity);
}
