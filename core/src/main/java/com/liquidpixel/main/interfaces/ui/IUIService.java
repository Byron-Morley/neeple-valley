package com.liquidpixel.main.interfaces.ui;

import com.badlogic.gdx.utils.Array;
import com.liquidpixel.main.model.item.StorageItem;

public interface IUIService {
    boolean isMouseOverUI();
     int getIndexOfStorageItem(Array<StorageItem> items, String name);
}
