package com.liquidpixel.main.ui.controller;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.liquidpixel.main.engine.GameState;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.interfaces.ui.IWindowService;
import com.liquidpixel.main.ui.view.pauseMenu.PauseMenuUI;
import com.liquidpixel.main.ui.view.workOrder.WorkOrderUI;

import static com.liquidpixel.main.utils.events.Messages.*;
import static com.liquidpixel.main.utils.ui.Windows.*;

public class MenuController implements Telegraph {

    PauseMenuUI pauseMenuUI;
    WorkOrderUI workOrderUI;
    ISelectionService selectionService;

    public MenuController(IWindowService windowService, ISelectionService selectionService) {
        this.selectionService = selectionService;
        MessageManager.getInstance().addListeners(this,
            TOGGLE_PAUSE_MENU,
            TOGGLE_WORK_ORDERS,
            TOGGLE_PEOPLE_UI
        );
        pauseMenuUI = windowService.getWindow(PAUSE_MENU);
        workOrderUI = windowService.getWindow(WORK_ORDER_UI);
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        switch (msg.message) {
            case TOGGLE_PAUSE_MENU:
                handleTogglePauseMenu();
                break;
            case TOGGLE_WORK_ORDERS:
                handleToggleWorkOrderMenu();
                break;
            case TOGGLE_PEOPLE_UI:
                handleTogglePeopleUI();
                break;
        }
        return false;
    }

    private void handleTogglePeopleUI() {
    }

    private void handleToggleWorkOrderMenu(){
        if(workOrderUI.isVisible()) {
            workOrderUI.setVisible(false);
        } else {
            workOrderUI.setVisible(true);
        }
    }

    private void handleTogglePauseMenu() {
        System.out.println("TOGGLE_PAUSE_MENU");
        if (!pauseMenuUI.isVisible()) {
            pauseMenuUI.setVisible(true);
            GameState.setPaused(true);
        } else {
            pauseMenuUI.setVisible(false);
            GameState.setPaused(false);
        }
    }
}
