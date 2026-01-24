package com.liquidpixel.main.interfaces.managers;

import com.liquidpixel.item.factories.ItemFactory;
import com.liquidpixel.main.interfaces.services.*;

public interface IItemManager {
    IItemService getItemService();
    ItemFactory getItemFactory();
}
