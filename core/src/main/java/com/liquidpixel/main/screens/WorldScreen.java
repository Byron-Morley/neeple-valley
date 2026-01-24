package com.liquidpixel.main.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.ai.pathfinding.MapGraph;
import com.liquidpixel.main.ai.tasks.TaskManager;
import com.liquidpixel.main.components.BodyComponent;
import com.liquidpixel.main.components.render.RenderComponent;
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
import com.liquidpixel.main.listeners.build.CollisionBuildListener;
import com.liquidpixel.main.listeners.build.ObstacleBuildListener;
import com.liquidpixel.main.managers.ClickBehaviorManager;
import com.liquidpixel.main.managers.ColonyManager;
import com.liquidpixel.main.managers.EntityIdManager;
import com.liquidpixel.main.managers.LoadAndSaveManager;
import com.liquidpixel.main.managers.core.MapManager;
import com.liquidpixel.main.managers.core.*;
import com.liquidpixel.main.managers.core.ui.UserInterfaceManager;
import com.liquidpixel.main.managers.market.MarketLedger;
import com.liquidpixel.main.managers.world.*;
import com.liquidpixel.main.services.*;
import com.liquidpixel.main.systems.*;
import com.liquidpixel.item.systems.EntityPickupSystem;
import com.liquidpixel.main.systems.ai.*;
import com.liquidpixel.main.systems.colony.ColonySystem;
import com.liquidpixel.main.systems.colony.ImmigrationSystem;
import com.liquidpixel.main.systems.farming.FarmSystem;
import com.liquidpixel.main.systems.farming.GrowSystem;
import com.liquidpixel.main.systems.inits.BuildingDoorInitSystem;
import com.liquidpixel.main.systems.inits.BuildingInitSystem;
import com.liquidpixel.main.systems.inits.ClickBehaviorInitSystem;
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
import com.liquidpixel.sprite.api.factory.IAnimationFactory;
import com.liquidpixel.sprite.api.factory.ISpriteComponentFactory;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;
import com.liquidpixel.sprite.system.AnimableSpriteSystem;
import com.liquidpixel.main.systems.work.WorkProcessingSystem;
import com.liquidpixel.main.systems.work.WorkerAssignmentSystem;


public class WorldScreen implements Screen, Initializable, GameSetup {

    public final static int WORLD_WIDTH = 128;
    public final static int WORLD_HEIGHT = 128;
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

    //    MODULES
    ISpriteAnimationModule spriteAnimationModule;

    //    FACTORIES
    AgentFactory agentFactory;
    MapFactory mapFactory;
    ISpriteComponentFactory spriteComponentFactory;
    IAnimationFactory animationFactory;
    ISpriteFactory spriteFactory;

    IClickBehaviorManager clickBehaviorManager;
    //    SERVICES
    IAgentService agentService;

    //    LISTENERS
    ObstacleBuildListener obstacleBuildListener;

    private boolean initialized = false;

    public WorldScreen(GameResources resources) {
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
        viewport.setUnitsPerPixel(1f / UI_SCALE);
        this.stage = new Stage(viewport);
        GameResources.get().setStage(stage);
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void initializeModules() {
        spriteAnimationModule = new SpriteAnimationModule(
            "assets/model/textures/atlas.yaml",
            "assets/raw/ramps/ramps.yaml",
            "assets/model/entities/animations.yaml"
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
        this.selectionManager = new SelectionManager(playerInputManager.getPlayerInputService(), itemManager.getItemService(), spriteFactory);
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
            null,// No scenario service in WorldScreen
            spriteFactory
        );
        playerInputManager.setUiService(this.userInterfaceManager.getUiService());
        this.levelManager = new WorldLevelManager(
            this.agentService,
            itemManager.getItemService(),
            mapManager.getMapService(),
            selectionManager.getSelectionService(),
            selectionManager.getSettlementService()
        );

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
            RenderComponent.class,
            PositionComponent.class,
            BodyComponent.class
        ).exclude(SpectralPickupComponent.class).get(), obstacleBuildListener);

        engine.addEntityListener(Family.all(
            RenderComponent.class,
            PositionComponent.class
        ).exclude(
            BodyComponent.class,
            SpectralPickupComponent.class
        ).get(), new CollisionBuildListener(mapManager.getWorldMap()));

        engine.addEntityListener(Family.all(ItemComponent.class).get(), (EntityListener) this.itemManager);
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
        engine.addSystem(new AnimableSpriteSystem());


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
        engine.addSystem(new TileRenderSystem(itemManager.getItemService()));
        engine.addSystem(new TileSelectionRenderSystem(itemManager.getItemService(), selectionManager.getSelectionService(), mapManager.getMapService().getWorldMap()));
        engine.addSystem(new ShapeRenderSystem());


//                engine.addSystem(new RenderSystem());
//        engine.addSystem(new OptimisedRenderSystem());
        engine.addSystem(new BatchedRenderSystem());


        engine.addSystem(new DebugRenderSystem((MapGraph) mapManager.getWorldMap()));
        engine.addSystem(new MapUpdateSystem(mapManager.getMapService(), this.obstacleBuildListener));
        engine.addSystem(new ColonySystem(selectionManager.getSettlementService()));
        engine.addSystem(new ImmigrationSystem(agentService));
        engine.addSystem(new HarvestProviderSystem(selectionManager.getStorageService(), spriteFactory));
        engine.addSystem(new ProviderWorkCreationSystem(selectionManager.getStorageService()));
        engine.addSystem(new HarvestWorkCreationSystem());
        engine.addSystem(new WorkerAssignmentSystem(selectionManager.getStorageService(), mapManager.getMapService(), itemManager.getItemService()));
        engine.addSystem(new WorkProcessingSystem(selectionManager.getStorageService(), mapManager.getMapService(), itemManager.getItemService()));
        engine.addSystem(new CreateConsumerWorkSystem(selectionManager.getStorageService(), selectionManager.getSettlementService()));
        engine.addSystem(new BuildingSystem(itemManager.getItemService(), selectionManager.getSettlementService()));
        engine.addSystem(new PopulationUpdateSystem());
        engine.addSystem(new AutoAssignJobSystem());
        engine.addSystem(new AutoAssignHouseSystem());
        engine.addSystem(new AutoAssignResourcesToAreaSystem());
        engine.addSystem(new StorageRenderSystem());

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


    }

    public void initializeGame() {
        worldMap = mapManager.getMapService().getWorldMap();
        ((Initializable) mapManager.getMapService()).init();
    }

    @Override
    public void reset() {
        cameraManager.resetCamera(agentService, mapConfig);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
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

    @Override
    public void resize(int width, int height) {
        cameraManager.resize(width, height);
        stage.getViewport().update(width, height, true);
        userInterfaceManager.resize();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
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
