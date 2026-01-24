package com.liquidpixel.main.ui.controller;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.interfaces.ui.IWindowService;
import com.liquidpixel.main.ui.view.debug.topRight.TopRightDebugUI;
import com.liquidpixel.main.utils.events.Messages;

import static com.liquidpixel.main.utils.ui.Windows.TOP_RIGHT_DEBUG;

public class OverlayUIController implements Telegraph {
    IWindowService windowService;
    TopRightDebugUI topRightDebugUI;

    public OverlayUIController(IWindowService windowService) {
        this.windowService = windowService;
        topRightDebugUI = windowService.getWindow(TOP_RIGHT_DEBUG);
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        switch (msg.message) {
            case Messages.CURSOR_TILE_POSITION:
                topRightDebugUI.setPositionLabel((GridPoint2) msg.extraInfo);
                break;
            case Messages.IS_WALKABLE:
                topRightDebugUI.setLabelThree((String) msg.extraInfo);
                break;
            case Messages.CONNECTION_COUNT:
                topRightDebugUI.setLabelFour((String) msg.extraInfo);
                break;
            case Messages.HOVER_ENTITY:
                topRightDebugUI.setLabelFive((String) msg.extraInfo);
                break;
            case Messages.SELECTED_ENTITY:
                topRightDebugUI.setLabelSix((String) msg.extraInfo);
                break;
            case Messages.ACTION:
                topRightDebugUI.setLabelSeven((String) msg.extraInfo);
                break;
        }
        return false;
    }
}
