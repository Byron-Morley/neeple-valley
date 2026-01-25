package com.liquidpixel.main.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.ai.pathfinding.MapGraph;
import com.liquidpixel.main.ai.tasks.TaskManager;
import com.liquidpixel.main.components.BodyComponent;
import com.liquidpixel.item.components.SpectralPickupComponent;
import com.liquidpixel.main.components.ai.BehaviorComponent;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.main.engine.GameClock;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.engine.GameState;
import com.liquidpixel.main.factories.AgentFactory;
import com.liquidpixel.main.factories.Factories;
import com.liquidpixel.main.factories.MapFactory;
import com.liquidpixel.main.generators.map.MapConfig;
import com.liquidpixel.main.interfaces.*;
import com.liquidpixel.main.interfaces.managers.*;
import com.liquidpixel.main.interfaces.services.IAgentService;
import com.liquidpixel.main.interfaces.services.IScenarioService;
import com.liquidpixel.main.interfaces.ui.IResizePosition;
import com.liquidpixel.main.listeners.build.ObstacleBuildListener;
import com.liquidpixel.main.managers.ClickBehaviorManager;
import com.liquidpixel.main.managers.ColonyManager;
import com.liquidpixel.main.managers.EntityIdManager;
import com.liquidpixel.main.managers.LoadAndSaveManager;
import com.liquidpixel.main.managers.core.*;
import com.liquidpixel.main.managers.core.ui.UserInterfaceManager;
import com.liquidpixel.main.managers.market.MarketLedger;
import com.liquidpixel.main.managers.world.WorldLevelManager;
import com.liquidpixel.main.providers.GameSpriteProvider;
import com.liquidpixel.main.services.AgentService;
import com.liquidpixel.main.services.ScenarioService;
import com.liquidpixel.main.systems.farming.FarmSystem;
import com.liquidpixel.main.systems.farming.GrowSystem;
import com.liquidpixel.main.systems.inits.BuildingDoorInitSystem;
import com.liquidpixel.main.systems.inits.BuildingInitSystem;
import com.liquidpixel.main.systems.inits.ClickBehaviorInitSystem;
import com.liquidpixel.main.systems.tasks.FishingSystem;
import com.liquidpixel.main.ui.view.scenario.ScenarioSelectionUI;
import com.liquidpixel.main.ui.view.agent.AgentUI;
import com.liquidpixel.main.ui.view.items.ItemsUI;
import com.liquidpixel.main.ui.view.scenarioState.ScenarioStateUI;
import com.liquidpixel.main.systems.*;
import com.liquidpixel.item.systems.EntityPickupSystem;
import com.liquidpixel.main.systems.ai.*;
import com.liquidpixel.main.systems.colony.ColonySystem;
import com.liquidpixel.main.systems.colony.ImmigrationSystem;
import com.liquidpixel.main.systems.load.CreateFoundationFencesSystem;
import com.liquidpixel.main.systems.render.*;
import com.liquidpixel.main.systems.settlement.*;
import com.liquidpixel.main.systems.spectral.SpectralInputSystem;
import com.liquidpixel.main.systems.spectral.SpectralMovementSystem;
import com.liquidpixel.main.systems.spectral.SpectralSelectionSystem;
import com.liquidpixel.main.systems.spectral.TerrainPaintSystem;
import com.liquidpixel.item.managers.ItemManager;
import com.liquidpixel.pathfinding.systems.MovementTaskSystem;
import com.liquidpixel.pathfinding.systems.TraverseSystem;
import com.liquidpixel.selection.api.IClickBehaviorManager;
import com.liquidpixel.sprite.SpriteAnimationModule;
import com.liquidpixel.sprite.api.ISpriteAnimationModule;
import com.liquidpixel.sprite.api.provider.ISpriteComponentProvider;
import com.liquidpixel.sprite.api.factory.IAnimationFactory;
import com.liquidpixel.sprite.api.factory.ISpriteComponentFactory;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;
import com.liquidpixel.main.systems.work.WorkProcessingSystem;
import com.liquidpixel.main.systems.work.WorkerAssignmentSystem;


public class ScenarioBuilder implements Screen, Initializable, GameSetup {

    public final static int WORLD_WIDTH = 64;
    public final static int WORLD_HEIGHT = 64;
    public final static int CHUNK_WIDTH = 16;
    public final static int CHUNK_HEIGHT = 16;
    public final static float UI_SCALE = 1f;

    //Settlement stats
    public final static float STACK_TO_CARRY_RATIO = 5f;

    //    CORE
    GameResources resources;
    Stage stage;
    Factories factories;
    MapConfig mapConfig;
    IWorldMap worldMap;

    //    MANAGERS
    TaskManager taskManager;
    CameraManager cameraManager;
    PlayerInputManager playerInputManager;
    UserInterfaceManager userInterfaceManager;
    LoadAndSaveManager loadAndSaveManager;
    EntityIdManager entityIdManager;

    IBehaviorManager behaviorManager;
    LevelManager levelManager;
    ISelectionManager selectionManager;
    IItemManager itemManager;
    IMapManager mapManager;
    IColonyManager colonyManager;
    IMarketLedger marketLedger;
    IClickBehaviorManager clickBehaviorManager;

    //    PROVIDERS
    ISpriteComponentProvider spriteProvider;


    //    SERVICES
    IAgentService agentService;
    IScenarioService scenarioService;


    //    MODULES
    ISpriteAnimationModule spriteAnimationModule;

    //    FACTORIES
    AgentFactory agentFactory;
    MapFactory mapFactory;
    ISpriteFactory spriteFactory;
    ISpriteComponentFactory spriteComponentFactory;
    IAnimationFactory animationFactory;

    //    LISTENERS
    ObstacleBuildListener obstacleBuildListener;

    //    UI
    ScenarioSelectionUI scenarioSelectionUI;
    private boolean lastScenarioState = false;

    private boolean initialized = false;

    public ScenarioBuilder(GameResources resources) {
        this.resources = resources;
        entityIdManager = new EntityIdManager();
        resources.getEngine().addEntityListener(entityIdManager);
        mapConfig = new MapConfig(WORLD_WIDTH, WORLD_HEIGHT, CHUNK_WIDTH, CHUNK_HEIGHT);
        initializeLogs();
        initializeStage();
        initializeModules();
        initializeFactories();
        initializeServices();
        initializeManagers();
        initializeListeners();
        initializeSystems();
        initializeGame();
        initializeUI();
    }

    public void initializeLogs() {

//    int LOG_NONE = 0;
//    int LOG_DEBUG = 3;
//    int LOG_INFO = 2;
//    int LOG_ERROR = 1;

        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Gdx.app.debug("GENERAL", "Debug Log Active");
        Gdx.app.log("GENERAL", "Info Log Active");
        Gdx.app.error("GENERAL", "Error Log Active");
    }

    public void initializeStage() {
        ScreenViewport viewport = new ScreenViewport();
        viewport.setUnitsPerPixel(1f / 1);
        this.stage = new Stage(viewport);
        GameResources.get().setStage(stage);
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void initializeModules() {
        spriteAnimationModule = new SpriteAnimationModule(
            "assets/model/textures/atlas.json",
            "assets/sprites/ramps/ramps.json",
            "assets/model/entities/animations.json"
        );
    }

    public void initializeFactories() {

        spriteComponentFactory = spriteAnimationModule.getSpriteComponentFactory();
        animationFactory = spriteAnimationModule.getAnimationFactory();
        spriteFactory = spriteAnimationModule.getSpriteFactory();

        this.mapFactory = new MapFactory(resources.getEngine());
        this.agentFactory = new AgentFactory(spriteComponentFactory, animationFactory);
    }

    public void initializeServices() {
        agentService = new AgentService(agentFactory);
    }

    public void initializeManagers() {
        this.cameraManager = new CameraManager();
        this.taskManager = new TaskManager(this);
        this.itemManager = new ItemManager(spriteComponentFactory, animationFactory, spriteFactory);
        this.mapManager = new MapManager(this, mapConfig, taskManager, itemManager.getItemService(), spriteFactory);
        this.behaviorManager = new BehaviorManager(mapManager.getMapService(), this.agentService, itemManager.getItemService());
        this.playerInputManager = new PlayerInputManager();
        this.selectionManager = new SelectionManager(
            playerInputManager.getPlayerInputService(),
            itemManager.getItemService(),
            spriteFactory
        );
        this.entityIdManager.setSettlementService(selectionManager.getSettlementService());
        this.marketLedger = new MarketLedger();
        this.loadAndSaveManager = new LoadAndSaveManager();
        this.clickBehaviorManager = new ClickBehaviorManager();
        this.userInterfaceManager = new UserInterfaceManager(
            itemManager.getItemService(),
            this.agentService,
            this.mapManager.getMapService(),
            this.selectionManager.getSelectionService(),
            cameraManager.getCameraService(),
            loadAndSaveManager,
            marketLedger.getTradingService(),
            selectionManager.getSettlementService(),
            clickBehaviorManager.getClickBehaviorService(),
            null, // scenarioService will be set later in initializeUI
            spriteFactory
        );
        playerInputManager.setUiService(this.userInterfaceManager.getUiService());
        this.levelManager = new WorldLevelManager(
            this.agentService,
            itemManager.getItemService(),
            mapManager.getMapService(),
            selectionManager.getSelectionService(),
            selectionManager.getSettlementService(),
            spriteAnimationModule
        );

        loadAndSaveManager.init(
            mapManager.getMapService(),
            itemManager.getItemService(),
            userInterfaceManager.getWindowService(),
            selectionManager.getSelectionService(),
            clickBehaviorManager.getClickBehaviorService()
        );
        this.colonyManager = new ColonyManager(itemManager.getItemService());
    }

    public void initializeListeners() {

        obstacleBuildListener = new ObstacleBuildListener(mapManager.getWorldMap());

        Engine engine = resources.getEngine();

        engine.addEntityListener(Family.all(
            PositionComponent.class,
            BodyComponent.class
        ).exclude(SpectralPickupComponent.class).get(), obstacleBuildListener);

//        engine.addEntityListener(Family.all(
//            RenderComponent.class,
//            PositionComponent.class
//        ).exclude(
//            BodyComponent.class,
//            SpectralPickupComponent.class
//        ).get(), new CollisionBuildListener(mapManager.getWorldMap()));

        engine.addEntityListener(Family.all(ItemComponent.class).get(), (EntityListener) this.itemManager);
//        engine.addEntityListener(Family.all(MovementTaskComponent.class).get(), (EntityListener) this.selectionManager);
        engine.addEntityListener(Family.all(BehaviorComponent.class).get(), (EntityListener) this.behaviorManager);

        this.stage.addListener(playerInputManager);
    }

    public void initializeSystems() {
        Engine engine = resources.getEngine();

        //INITIALIZATION SYSTEMS
        engine.addSystem(new BuildingDoorInitSystem(itemManager.getItemService()));
        engine.addSystem(new BuildingInitSystem(mapManager.getWorldMap()));
        engine.addSystem(new ClickBehaviorInitSystem(clickBehaviorManager.getClickBehaviorService()));

        //UPDATE SYSTEMS

        //Farming
        engine.addSystem(new FarmSystem(itemManager.getItemService()));
        engine.addSystem(new GrowSystem(selectionManager.getStorageService(), spriteFactory));

        //Rendering
        engine.addSystem(new FadeSystem());


        spriteAnimationModule.registerSystems(
            engine,
            new GameSpriteProvider(itemManager.getItemService())
        );

        //Other
        engine.addSystem(new CameraFocusSystem(cameraManager.getCameraService()));
        engine.addSystem(new SpectralSelectionSystem(
            playerInputManager.getPlayerInputService(),
            cameraManager.getCameraService(),
            userInterfaceManager.getUiService(),
            selectionManager.getSelectionService(),
            itemManager.getItemService(),
            mapManager.getMapService(),
            userInterfaceManager.getWindowService()
        ));
        engine.addSystem(new EntityPickupSystem(cameraManager.getCameraService()));
        engine.addSystem(new EquipmentSystem());
        engine.addSystem(new HarvestSystem(itemManager.getItemService(), mapManager.getMapService().getWorldMap(), selectionManager.getSelectionService()));
        engine.addSystem(new HaulSystem(mapManager.getMapService(), itemManager.getItemService(), selectionManager.getStorageService()));
        engine.addSystem(new MovementTaskSystem(mapManager.getMapService(), taskManager));
        engine.addSystem(new WorkTaskSystem());
        engine.addSystem(new DoWorkSystem(mapManager.getMapService(), itemManager.getItemService()));
        engine.addSystem(new TraverseSystem(mapManager.getMapService()));
        engine.addSystem(new CarryingSystem());
        engine.addSystem(new FollowSystem());

        engine.addSystem(new SpectralMovementSystem());
        engine.addSystem(new SpectralInputSystem(playerInputManager));

        engine.addSystem(new CreateFoundationFencesSystem(itemManager.getItemService(), mapManager.getMapService().getWorldMap()));
        engine.addSystem(new FollowSystem());
        engine.addSystem(new TileRenderSystem(itemManager.getItemService()));
        engine.addSystem(new TileSelectionRenderSystem(itemManager.getItemService(), selectionManager.getSelectionService(), mapManager.getMapService().getWorldMap()));

//        engine.addSystem(new RenderSystem());
        engine.addSystem(new ShapeRenderSystem());
        engine.addSystem(new BatchedRenderSystem());


        engine.addSystem(new DebugRenderSystem((MapGraph) mapManager.getWorldMap()));
        engine.addSystem(new MapUpdateSystem(mapManager.getMapService(), this.obstacleBuildListener));
        engine.addSystem(new ColonySystem(selectionManager.getSettlementService()));
        engine.addSystem(new ImmigrationSystem(agentService));
        engine.addSystem(new HarvestProviderSystem(selectionManager.getStorageService(), spriteFactory));

        engine.addSystem(new HarvestWorkCreationSystem(itemManager.getItemService()));
        engine.addSystem(new CreateConsumerWorkSystem(selectionManager.getStorageService(), selectionManager.getSettlementService()));
        engine.addSystem(new ProviderWorkCreationSystem(selectionManager.getStorageService()));

        engine.addSystem(new WorkerAssignmentSystem(selectionManager.getStorageService(), mapManager.getMapService(), itemManager.getItemService()));
        engine.addSystem(new WorkProcessingSystem(selectionManager.getStorageService(), mapManager.getMapService(), itemManager.getItemService()));
        engine.addSystem(new BuildingSystem(itemManager.getItemService(), selectionManager.getSettlementService()));
        engine.addSystem(new PopulationUpdateSystem());
        engine.addSystem(new AutoAssignJobSystem());
        engine.addSystem(new AutoAssignHouseSystem());
        engine.addSystem(new AutoAssignResourcesToAreaSystem());
        engine.addSystem(new StorageRenderSystem(spriteFactory));
        engine.addSystem(new EntitySelectionSystem(itemManager.getItemService()));

        engine.addSystem(new ResourceSelectionRenderSystem(selectionManager.getSelectionService(), cameraManager.getCameraService()));
        engine.addSystem(new SpawnConfigurationSystem(
            cameraManager.getCameraService(),
            playerInputManager.getPlayerInputService(),
            itemManager.getItemService(),
            selectionManager.getSelectionService(),
            selectionManager.getSettlementService()
        ));
        engine.addSystem(new TerrainPaintSystem(cameraManager.getCameraService(), mapManager.getMapService().getWorldMap(), itemManager.getItemService()));
        engine.addSystem(new TerrainItemSystem(mapManager.getMapService().getTerrainItemService()));


        engine.addSystem(new EnterDoorSystem());
        engine.addSystem(new ExitDoorSystem());
        engine.addSystem(new FishingSystem());
    }

    public void initializeGame() {

        clickBehaviorManager.init(
            itemManager.getItemService(),
            mapManager.getMapService(),
            cameraManager.getCameraService(),
            mapManager.getMapService().getWorldMap(),
            userInterfaceManager.getWindowService(),
            selectionManager.getSelectionService(),
            selectionManager.getSettlementService(),
            obstacleBuildListener
        );

        worldMap = mapManager.getMapService().getWorldMap();
        ((Initializable) mapManager.getMapService()).init();
    }

    public void initializeUI() {
        scenarioService = new ScenarioService(
            mapManager.getMapService(),
            worldMap,
            selectionManager.getSelectionService(),
            selectionManager.getSettlementService(),
            agentService,
            itemManager.getItemService(),
            selectionManager.getStorageService()
        );

        // Set the scenarioService in UserInterfaceManager now that it's created
        userInterfaceManager.setScenarioService(scenarioService);

        scenarioSelectionUI = new ScenarioSelectionUI(scenarioService);
        stage.addActor(scenarioSelectionUI.get());
        scenarioSelectionUI.setVisible(true);

        // Get UI components from the main UI manager and set up their visibility
        ScenarioStateUI scenarioStateUI = userInterfaceManager.getScenarioStateUI();
        AgentUI agentUI = userInterfaceManager.getAgentUI();
        ItemsUI itemsUI = userInterfaceManager.getItemsUI();

        // Set default visibility states
        if (scenarioStateUI != null) {
            scenarioStateUI.setVisible(false); // Hide by default, will show when scenario is loaded
        }
        if (agentUI != null) {
            agentUI.setVisible(false); // Hide by default, toggle with 'P' key
        }
        if (itemsUI != null) {
            itemsUI.setVisible(false); // Hide by default, toggle with 'I' key
        }
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // Handle key input for toggling UIs
        handleKeyInput();

        // Check for scenario state changes
        checkScenarioStateChange();

        if (worldMap.isReady()) {
            if (GameState.isPaused()) {
                taskManager.tick(delta);
                userInterfaceManager.render(delta);
            } else {

                float scaledDelta = delta * GameState.getTimeScale();

                GameClock.update(scaledDelta);
                cameraManager.render(scaledDelta, resources.getBatch());

                resources.getBatch().begin();
                worldMap.render(scaledDelta);
                resources.getEngine().update(scaledDelta);
                resources.getBatch().end();

                behaviorManager.tick(scaledDelta);
                taskManager.tick(scaledDelta);
                userInterfaceManager.render(scaledDelta);
            }
        } else {
            taskManager.tick(delta);
        }
    }

    private void handleKeyInput() {
        // Toggle Agent UI with 'P' key
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            AgentUI agentUI = userInterfaceManager.getAgentUI();
            if (agentUI != null) {
                agentUI.setVisible(!agentUI.isVisible());
            }
        }

        // Toggle Items UI with 'I' key
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            ItemsUI itemsUI = userInterfaceManager.getItemsUI();
            if (itemsUI != null) {
                itemsUI.setVisible(!itemsUI.isVisible());
            }
        }
    }

    private void checkScenarioStateChange() {
        if (scenarioService != null) {
            boolean currentScenarioState = scenarioService.hasCurrentScenario();

            // If a scenario was just loaded (transition from false to true)
            if (!lastScenarioState && currentScenarioState) {
                showScenarioStateUI();
            }

            lastScenarioState = currentScenarioState;
        }
    }

    private void showScenarioStateUI() {
        ScenarioStateUI scenarioStateUI = userInterfaceManager.getScenarioStateUI();
        if (scenarioStateUI != null) {
            scenarioStateUI.setVisible(true);
            // Ensure proper positioning when shown
            scenarioStateUI.resize();
            System.out.println("Scenario loaded - showing scenario state UI");
        }
    }

    @Override
    public void resize(int width, int height) {
        cameraManager.resize(width, height);
        stage.getViewport().update(width, height, true);
        userInterfaceManager.resize();

        // ScenarioSelectionUI handles its own positioning through IResizePosition
        if (scenarioSelectionUI != null && scenarioSelectionUI instanceof IResizePosition) {
            ((IResizePosition) scenarioSelectionUI).resize();
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    public void reset() {
        cameraManager.resetCamera(agentService, mapConfig);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        worldMap.dispose();
    }

    @Override
    public void init() {
        if (!initialized) {
            cameraManager.resetCamera(agentService, mapConfig);
            userInterfaceManager.init();
            levelManager.init();
            initialized = true;
        }
    }
}
