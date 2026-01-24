package com.liquidpixel.main.listeners.item;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.selection.api.IClickBehavior;
import com.liquidpixel.main.interfaces.ui.IWindowService;
import com.liquidpixel.main.ui.view.farmUI.FarmingUI;

import static com.liquidpixel.main.utils.ui.Windows.FARMING_WINDOW;

public class FarmClickBehavior implements IClickBehavior {


    IWindowService windowService;

    public FarmClickBehavior(IWindowService windowService) {
        this.windowService = windowService;
    }

    @Override
    public void onClick(Entity entity) {
        FarmingUI farmingUI = windowService.getWindow(FARMING_WINDOW);
        farmingUI.init();
        System.out.println("FarmClickBehavior");
    }
}

