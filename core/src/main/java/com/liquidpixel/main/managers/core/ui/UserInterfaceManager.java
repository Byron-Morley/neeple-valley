package com.liquidpixel.main.managers.core.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.interfaces.*;
import com.liquidpixel.main.interfaces.managers.ILoadAndSaveManager;
import com.liquidpixel.main.interfaces.managers.IUIManager;
import com.liquidpixel.main.interfaces.managers.IWindowManager;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.main.interfaces.ui.IResizePosition;
import com.liquidpixel.main.interfaces.ui.IWindowService;
import com.liquidpixel.main.services.ui.UIService;
import com.liquidpixel.main.services.ui.WindowService;
import com.liquidpixel.main.ui.controller.MenuController;
import com.liquidpixel.main.ui.controller.SelectionUIController;
import com.liquidpixel.main.ui.view.debug.DebugOverlayManager;
import com.liquidpixel.main.ui.view.debug.SpawnMenuUI;
import com.liquidpixel.main.ui.view.farmUI.FarmingUI;
import com.liquidpixel.main.ui.view.infoPanel.InfoPanelUI;
import com.liquidpixel.main.ui.view.windows.UniversalWindow;
import com.liquidpixel.main.ui.view.menu.MenuUI;
import com.liquidpixel.main.ui.view.panels.CostUI;
import com.liquidpixel.main.ui.view.pauseMenu.PauseMenuUI;
import com.liquidpixel.main.ui.view.resources.ResourcesUI;
import com.liquidpixel.main.ui.view.infoPanel.storage.StorageUI;
import com.liquidpixel.main.ui.view.time.GameClockUI;
import com.liquidpixel.main.ui.view.workOrder.WorkOrderUI;
import com.liquidpixel.main.ui.view.agent.AgentUI;
import com.liquidpixel.main.ui.view.items.ItemsUI;
import com.liquidpixel.main.ui.view.scenarioState.ScenarioStateUI;
import com.liquidpixel.main.ui.view.debug.IconButtonUI;
import com.liquidpixel.main.ui.view.windows.ConfirmationDialog;
import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.selection.api.IClickBehaviorService;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;

import java.util.HashMap;
import java.util.Map;

import static com.liquidpixel.main.utils.ui.Windows.*;

public class UserInterfaceManager implements Renderable, IUIManager, IWindowManager, Updatable, IResizePosition {

    final Map<Integer, IGet<Group>> windows;
    final Stage stage;
    public boolean initialised = false;

    //    MANAGERS
    DebugOverlayManager debugOverlayManager;
    ILoadAndSaveManager loadAndSaveManager;

    //    SERVICES
    IMapService mapService;
    ICameraService cameraService;
    IItemService itemService;
    IAgentService agentService;
    IWindowService windowService;
    UIService uiService;
    ISelectionService selectionService;
    ITradingService tradingService;
    ISettlementService settlementService;
    IClickBehaviorService clickBehaviorService;
    IScenarioService scenarioService;

    ISpriteFactory spriteFactory;

    //    CONTROLLERS
    SelectionUIController selectionUIController;
    MenuController menuController;


    public UserInterfaceManager(
        IItemService itemService,
        IAgentService agentService,
        IMapService mapService,
        ISelectionService selectionService,
        ICameraService cameraService,
        ILoadAndSaveManager loadAndSaveManager,
        ITradingService tradingService,
        ISettlementService settlementService,
        IClickBehaviorService clickBehaviorService,
        IScenarioService scenarioService,
        ISpriteFactory spriteFactory
    ) {
        this.stage = GameResources.get().getStage();
        this.itemService = itemService;
        this.agentService = agentService;
        this.selectionService = selectionService;
        this.cameraService = cameraService;
        this.clickBehaviorService = clickBehaviorService;
        this.mapService = mapService;
        this.windows = new HashMap<>();
        this.uiService = new UIService(this);
        this.windowService = new WindowService(this);
        this.loadAndSaveManager = loadAndSaveManager;
        this.tradingService = tradingService;
        this.settlementService = settlementService;
        this.scenarioService = scenarioService;
        this.spriteFactory = spriteFactory;
    }

    public void init() {
        if (!initialised) {
            initializeManagers();
            initializeViews();
            initializeControllers();
            initialised = true;
        }
    }

    private void initializeManagers() {
        this.debugOverlayManager = new DebugOverlayManager(this.stage, windowService);
    }

    public void registerWindow(int id, IGet<Group> window) {
        stage.addActor(window.get());
        windows.put(id, window);
    }

    @Override
    public IGet<Group> getWindow(int id) {
        return windows.get(id);
    }

    private void initializeViews() {

        System.out.println("Initializing UI");

        registerWindow(PAUSE_MENU, new PauseMenuUI(loadAndSaveManager));
//        registerWindow(MODE_BUTTONS, new ModeButtonsUI(selectionService));
//        registerWindow(GLOBAL_RESOURCES, new ResourcesUI(selectionService, itemService, settlementService));
//        registerWindow(TRADE_WINDOW, new TradeUI(selectionService, tradingService));
        registerWindow(COST_PANEL, new CostUI(settlementService));


        registerWindow(MENU_UI, new MenuUI(selectionService, cameraService, itemService, mapService.getWorldMap(), windowService, clickBehaviorService, spriteFactory));
//        registerWindow(INFORMATION_PANEL, new InformationPanelUI());
        registerWindow(SPAWN_MENU, new SpawnMenuUI(
            itemService,
            agentService,
            selectionService,
            cameraService
        ));


        registerWindow(INFO_PANEL, new InfoPanelUI(selectionService));
//        registerWindow(SELECTION_UI, new SelectionUI(selectionService, cameraService));
        registerWindow(STORAGE_UI, new StorageUI(windowService, itemService, uiService));
//        registerWindow(RECIPE_CHOICE, new RecipeListUI(itemService));
        registerWindow(GAME_CLOCK, new GameClockUI(mapService));
        registerWindow(WORK_ORDER_UI, new WorkOrderUI(settlementService));
        registerWindow(AGENT_UI, new AgentUI(agentService));
        registerWindow(ITEMS_UI, new ItemsUI(itemService));

        registerWindow(FARMING_WINDOW, new FarmingUI(selectionService));
        registerWindow(UNIVERSAL_WINDOW, new UniversalWindow("Window"));


        // Only register scenario state UI if scenarioService is available
        if (scenarioService != null) {
            registerWindow(SCENARIO_STATE_UI, new ScenarioStateUI(scenarioService));
        }

        // Register the small icon button in bottom right corner
        registerWindow(ICON_BUTTON_UI, new IconButtonUI(spriteFactory));

        // Register confirmation dialog
        registerWindow(CONFIRMATION_DIALOG, new ConfirmationDialog());
    }

    public void initializeControllers() {
        this.menuController = new MenuController(windowService, selectionService);
//        this.selectionUIController = new SelectionUIController(windowService);
//        this.storageUIController = new StorageUIController(windowService);
    }


    @Override
    public void render(float delta) {
        stage.getViewport().apply();
        stage.act();
        stage.draw();
        stage.setDebugAll(false);
        update();
    }


    @Override
    public void update() {
        for (IGet<Group> window : windows.values()) {
            if (window instanceof Updatable) {
                ((Updatable) window).update();
            }
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public DebugOverlayManager getDebugOverlayManager() {
        return debugOverlayManager;
    }

    @Override
    public UIService getUiService() {
        return uiService;
    }

    public IWindowService getWindowService() {
        return windowService;
    }

    public AgentUI getAgentUI() {
        return (AgentUI) windows.get(AGENT_UI);
    }

    public ItemsUI getItemsUI() {
        return (ItemsUI) windows.get(ITEMS_UI);
    }

    public ScenarioStateUI getScenarioStateUI() {
        return (ScenarioStateUI) windows.get(SCENARIO_STATE_UI);
    }


    public void setScenarioService(IScenarioService scenarioService) {
        this.scenarioService = scenarioService;
        // Re-register the scenario state UI with the new service
        if (scenarioService != null) {
            ScenarioStateUI existingUI = getScenarioStateUI();
            if (existingUI != null) {
                existingUI.get().remove(); // Remove from stage
                windows.remove(SCENARIO_STATE_UI);
            }
            registerWindow(SCENARIO_STATE_UI, new ScenarioStateUI(scenarioService));
        }
    }

    @Override
    public void resize() {
        for (IGet<Group> window : windows.values()) {
            if (window instanceof IResizePosition) {
                ((IResizePosition) window).resize();
            }
        }
    }

    public void reset() {

    }


}
