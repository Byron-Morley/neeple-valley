package com.liquidpixel.main.ui.view.debug;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.liquidpixel.main.interfaces.services.IDebugOverlayService;
import com.liquidpixel.main.interfaces.ui.IWindowService;
import com.liquidpixel.main.services.ui.DebugOverlayService;
import com.liquidpixel.main.systems.debug.DebugUISystem;
import com.liquidpixel.main.ui.controller.OverlayUIController;
import com.liquidpixel.main.ui.view.UIManagers;
import com.liquidpixel.main.ui.view.debug.topRight.TopRightDebugUI;
import com.liquidpixel.main.ui.view.interfaces.UIManager;
import com.liquidpixel.main.utils.events.Messages;

import static com.liquidpixel.main.utils.events.Messages.*;
import static com.liquidpixel.main.utils.ui.Windows.TOP_RIGHT_DEBUG;

public class DebugOverlayManager extends UIManagers implements UIManager, Telegraph {
    private OverlayUIController overlayUIController;
    IDebugOverlayService debugOverlayService;
    IWindowService windowService;

    public DebugOverlayManager(Stage stage, IWindowService windowService) {
        super(stage);
        this.windowService = windowService;

        MessageManager.getInstance().addListeners(this,
            TOGGLE_GRID,
            TOGGLE_CHUNK_BORDER,
            TOGGLE_PATH_NODES
        );

        initializeViews();
        initializeControllers();
        this.debugOverlayService = new DebugOverlayService();
        addUIToStage();
        engine.addSystem(new DebugUISystem(this.overlayUIController));
        subscribeListeners();
    }

    public void initializeViews() {
        windowService.getWindowManager().registerWindow(TOP_RIGHT_DEBUG, new TopRightDebugUI());
    }

    public void addUIToStage() {
    }

    public void initializeControllers() {
        this.overlayUIController = new OverlayUIController(windowService);
    }

    private void toggleMapGrid() {
    }

    private void toggleChunkBorder() {
    }

    private void togglePathNodes() {
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        switch (msg.message) {
            case Messages.TOGGLE_GRID:
                toggleMapGrid();
                break;
            case Messages.TOGGLE_CHUNK_BORDER:
                toggleChunkBorder();
                break;
            case Messages.TOGGLE_PATH_NODES:
                togglePathNodes();
                break;
        }
        return false;
    }

    private void subscribeListeners() {
        MessageDispatcher messageDispatcher = MessageManager.getInstance();
        messageDispatcher.addListeners(this.overlayUIController,
            CURSOR_TILE_POSITION,
            IS_WALKABLE,
            CONNECTION_COUNT,
            HOVER_ENTITY,
            SELECTED_ENTITY,
            ACTION
        );
    }
}
