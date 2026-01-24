package com.liquidpixel.main.ui.controller;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.liquidpixel.main.interfaces.ui.IWindowService;
import com.liquidpixel.main.ui.view.infoPanel.InfoPanelUI;
import com.liquidpixel.main.ui.view.selection.SelectionUI;

import static com.liquidpixel.main.utils.events.Messages.*;
import static com.liquidpixel.main.utils.ui.Windows.*;

public class SelectionUIController implements Telegraph {
    InfoPanelUI infoPanelUI;
    SelectionUI selectionUI;

    public SelectionUIController(IWindowService windowService) {
        MessageManager.getInstance().addListeners(this,
            UPDATE_SELECTION,
            UPDATE_STORAGE_LINK
        );
        this.infoPanelUI = windowService.getWindow(INFO_PANEL);
        this.selectionUI = windowService.getWindow(SELECTION_UI);
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        switch (msg.message) {
            case UPDATE_SELECTION:
                System.out.println("UPDATE_SELECTION");
//                infoPanelUI.selectionUpdate();
                break;
        }
        return false;
    }
}
