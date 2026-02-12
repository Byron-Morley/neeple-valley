package com.liquidpixel.main.ui.controller;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.liquidpixel.core.api.ui.IWindowService;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.ui.view.infoPanel.InfoPanelUI;
import com.liquidpixel.main.ui.view.infoPanel.storage.StoragePanelPresenter;

import static com.liquidpixel.main.utils.ui.Windows.INFO_PANEL;

public class StorageUIController {

    MessageDispatcher messageDispatcher;
    StoragePanelPresenter storagePanelPresenter;

    public StorageUIController(IWindowService windowService) {
        messageDispatcher = MessageManager.getInstance();
        InfoPanelUI infoPanelUI = windowService.getWindow(INFO_PANEL);
        storagePanelPresenter = infoPanelUI.getPresenter().getStoragePanelPresenter();
        storagePanelPresenter.setListener(createListener());

    }

    public StoragePanelPresenter.Listener createListener() {
        return new StoragePanelPresenter.Listener() {
            @Override
            public void onPriorityChanged(Entity entity, StorageComponent storageComponent, int newPriority) {
            }
        };
    }
}
