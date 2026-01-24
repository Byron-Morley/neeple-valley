package com.liquidpixel.main.listeners.item;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.selection.api.IClickBehavior;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.ui.IWindowService;
import com.liquidpixel.main.ui.view.tabs.FarmTab;
import com.liquidpixel.main.ui.view.tabs.StorageTab;
import com.liquidpixel.main.ui.view.windows.UniversalWindow;
import com.liquidpixel.main.utils.Mappers;

import static com.liquidpixel.main.utils.ui.Windows.UNIVERSAL_WINDOW;

public class WindowClickBehavior implements IClickBehavior {


    IWindowService windowService;
    IItemService itemService;

    public WindowClickBehavior(IWindowService windowService, IItemService itemService) {
        this.windowService = windowService;
        this.itemService = itemService;
    }

    @Override
    public void onClick(Entity entity) {
        UniversalWindow window = windowService.getWindow(UNIVERSAL_WINDOW);

        window.clearTabs();
        window.setVisible(true);


        if (Mappers.farm.has(entity)) {
            FarmTab farmTab = new FarmTab(entity, itemService, windowService);
            window.addTab(farmTab);
        }

        if(Mappers.storage.has(entity)) {
            window.addTab(new StorageTab(entity, windowService, itemService));
        }

        System.out.println("WindowClickBehavior");
    }
}

