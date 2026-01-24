package com.liquidpixel.main.listeners.item;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.selection.api.IClickBehavior;
import com.liquidpixel.main.interfaces.ui.IWindowService;
import com.liquidpixel.main.ui.view.infoPanel.InfoPanelUI;

import static com.liquidpixel.main.utils.ui.Windows.INFO_PANEL;

public class StorageClickBehavior implements IClickBehavior {

    IWindowService windowService;

    public StorageClickBehavior(IWindowService windowService) {
        this.windowService = windowService;
    }

    @Override
    public void onClick(Entity entity) {
        InfoPanelUI infoPanelUI = windowService.getWindow(INFO_PANEL);
        infoPanelUI.setEntity(entity);
        infoPanelUI.init();
    }
}
