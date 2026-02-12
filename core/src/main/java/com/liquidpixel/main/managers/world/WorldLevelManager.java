package com.liquidpixel.main.managers.world;

import com.liquidpixel.core.components.core.StatusComponent;
import com.liquidpixel.core.core.Action;
import com.liquidpixel.core.core.Direction;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.DoorComponent;
import com.liquidpixel.main.components.colony.BuildingComponent;
import com.liquidpixel.main.components.colony.ExitDoorComponent;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.helpers.BuildingHelper;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.main.managers.core.GameManager;
import com.liquidpixel.main.services.items.StorageHelper;
import com.liquidpixel.main.utils.LoopUtils;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.sprite.api.ISpriteAnimationModule;
import com.liquidpixel.sprite.api.services.IAnimationService;


import java.util.*;

import static com.liquidpixel.main.screens.WorldScreen.WORLD_WIDTH;

public class WorldLevelManager extends GameManager {
    IMapService mapService;
    IWorldMap worldMap;
    ISelectionService selectionService;
    ISettlementService settlementService;
    private ISpriteAnimationModule spriteModule;
    private IAnimationService animationService;
    Random random;
    int high = WORLD_WIDTH;
    int low = 1;
    Engine engine;

    public WorldLevelManager(
        IAgentService agentService,
        IItemService itemService,
        IMapService mapService,
        ISelectionService selectionService,
        ISettlementService settlementService,
        ISpriteAnimationModule spriteModule
    ) {
        super(agentService, itemService);
        this.settlementService = settlementService;
        this.selectionService = selectionService;
        this.mapService = mapService;
        this.spriteModule = spriteModule;
        worldMap = mapService.getWorldMap();
        this.random = new Random();
        engine = GameResources.get().getEngine();
    }

    public void init() {

        System.out.println("World Level Manager Initialized");

//        Entity item = settlementService.buildInSettlement("resources/stone",new GridPoint2(20,20), 20);


        SettlementComponent settlement = Mappers.settlement.get(selectionService.getSelectedSettlement());

        Entity person = agentService.spawnAgent(new GridPoint2(32, 32), "man");
        settlement.addPopulation(person);


        Entity warehouse1 = settlementService.buildInSettlement("storage/warehouse", new GridPoint2(20, 30));
        Entity warehouse2 = settlementService.buildInSettlement("storage/warehouse", new GridPoint2(32, 40));


        Entity item = settlementService.buildInSettlement("resources/stone",new GridPoint2(20,20), 80);
        IStorageItem storageItem = itemService.getStorageItem(item);

        StorageHelper.removeItem(item, storageItem);
        StorageHelper.addItem(warehouse1, storageItem);
        StorageHelper.setPriority(warehouse2, 3);


        System.out.println("item added");

//        Entity item = itemService.getItem("ui/blue_tint").build();
//        itemService.spawnItem(item, new GridPoint2(35, 35));

//        SettlementComponent settlement = Mappers.settlement.get(selectionService.getSelectedSettlement());
//        Entity house = settlementService.buildInSettlement("houses/house", new GridPoint2(20, 32));

//
//        Entity person = agentService.createAgent("population");

//        Entity door = itemService.getItem("houses/door").build();
//        itemService.spawnItem(door, new GridPoint2(32, 32));

//        GridPoint2 origin = new GridPoint2(0, 0);
//        LoopUtils.insideOut(16, 16, origin, (position) -> {
//            System.out.println(position);
//        });


//        exitWorkshop();

//        Entity person = agentService.spawnAgent(new GridPoint2(32, 32), "population");
//        settlement.addPopulation(person);


//        Entity tool = itemService.getItem("tools/rod").build();
//        itemService.spawnItem(tool, new GridPoint2(32, 32));

        System.out.println("item added");

//        spriteModule.setComponentFactory(new ComponentFactory());
//        ISpriteComponentFactory animationComponentFactory = spriteModule.getSpriteComponentFactory();
//        IAnimationFactory animationFactory = spriteModule.getAnimationFactory();
//        ISpriteFactory spriteFactory = spriteModule.getSpriteFactory();
//
//        Entity agent = spriteModule.createEntity("AGENT")
//            .at(32, 32)
//            .withLayer("human", 12)
//            .withLayer("mohawk", 18)
//            .withLayer("vest", 7)
//            .withLayer("boots", 8)
//            .named("player")
//            .build();
//        engine.addEntity(agent);
//
//        animationService = new AnimationService(agent);
//
//
//
//
//        Entity tool = new Entity();
//
//        String spriteName = "tools/rod";
//        GameSprite sprite = spriteFactory.getSprite(spriteName);
//
//        tool.add(new RenderComponent(sprite, new EquipmentRenderPositionStrategy(), RenderPriority.AGENT))
//            .add(new PositionComponent(32, 32))
//            .add(new StatusComponent(Action.IDLE, Direction.NONE))
//            .add(new SpriteComponent.Builder(spriteName).build())
//            .add(animationComponentFactory.createSpriteStackBuilderComponent())
//            .add(animationComponentFactory.createRefreshSpriteStackBuilderComponent())
//            .add(new AnimableSpriteComponent())
//            .add(new StackedSpritesComponent(animationFactory.get("ROD")))
//            .add(new RefreshSpriteRequirementComponent()
//            );
//
//        engine.addEntity(tool);
//
//        AnimableSpriteComponent axeAnimableSpriteComponent = tool.getComponent(AnimableSpriteComponent.class);
//        axeAnimableSpriteComponent.setTileType("player");
//        axeAnimableSpriteComponent.setSynchronized(true);
//
//
//
//        animationService.addListener(new AnimationService.AnimationListener() {
//            @Override
//            public void onFrameChanged(int frameIndex, boolean isFinished) {
//                System.out.println("Animation frame changed to: " + frameIndex);
//                if (isFinished) {
//                    AnimationState state = animationService.getAnimationState();
////                    animationService.setAnimation(new Status("CHOP_DOWN"));
////                    animationService.playAnimation();
//                    System.out.println("agent "+ agent);
//                    System.out.println("tool "+ tool);
//                    System.out.println("Animation finished");
//                }
//            }
//        });
//
//        animationService.setAnimation(new Status("CAST_DOWN"));


        //create a man
//        Entity agent = agentService.spawnAgent(new GridPoint2(32, 32), "man");
//
//        //create an item
//        Entity tool = itemService.getItem("tools/axe").build();
//        itemService.spawnItem(tool, new GridPoint2(32, 32));
//
//
//        EquipHelper equipHelper = new EquipHelper(agent, tool);
//        equipHelper.equip();
//
//
//        animationService = new AnimationService(agent);
//        animationService.addListener(new AnimationService.AnimationListener() {
//            @Override
//            public void onFrameChanged(int frameIndex, boolean isFinished) {
//                System.out.println("Animation frame changed to: " + frameIndex);
//                if (isFinished) {
//                    System.out.println("agent "+ agent);
//                    System.out.println("tool "+ tool);
//                    AnimationState state = animationService.getAnimationState();
//                    System.out.println("Animation finished");
////                    animationService.setAnimation(new Status("CHOP_DOWN"));
//                }
//            }
//        });
//        animationService.setAnimation(new Status("CHOP_DOWN"));


//
//        IAnimationService anim = new AnimationService(item);
//
//        anim.addListener(new AnimationService.AnimationListener() {
//            @Override
//            public void onFrameChanged(int frameIndex, boolean isFinished) {
//                System.out.println("Animation frame changed to: " + frameIndex);
//                System.out.println(anim.getAnimationState().toString());
//                anim.setAnimation(new Status("CHOP_DOWN"));
//            }
//        });
//
//        anim.setAnimation(new Status("CHOP_DOWN"));
//        anim.setCurrentFrame(1);
//
//        System.out.println("World Level Manager Initialized - Agent positioned to water");
//        Entity person1 = agentService.spawnAgent(new GridPoint2(32, 32), "population");


        //        Entity person2 = agentService.spawnAgent(new GridPoint2(34, 32), "woman");
//        Entity person3 = agentService.spawnAgent(new GridPoint2(36, 32), "example");


//        settlementService.buildInSettlement("storage/warehouse", new GridPoint2(68, 68));
//        settlementService.buildInSettlement("storage/warehouse", new GridPoint2(80, 68));
//        settlementService.buildInSettlement("storage/warehouse", new GridPoint2(53, 68));
//        itemService.spawnItem(itemService.getItem("resources/wood_log", 20).build(), new GridPoint2(34, 28));


//        IScenario scenario = new DoorScenario(mapService, worldMap, selectionService, settlementService, agentService, itemService);
//        scenario.start();

//        IRenderService renderService = new RenderService();
//        renderService.fadeIn(person, 5f);


//        //get the correct tool
//        Mappers.equipment.get(person).addEquipment(axe);
//
//        //equip the tool
//        AgentComponent agentComponent = Mappers.agent.get(person);
//        agentComponent.setEquipped(axe);
//
//        Entity equipped = agentComponent.getEquipped();
//        EquipableComponent equipable = Mappers.equipable.get(equipped);
//
//        Mappers.status.get(person).setAction(equipable.getAction());
//
//        Direction direction = Direction.LEFT;
//
//        setStatus(equipped, equipable.getAction(),direction);
//        setStatus(person, equipable.getAction(), direction);
//
//
//        AnimableSpriteComponent personAnimableSpriteComponent = Mappers.animableSprite.get(person);
//
//
//
//        AnimableSpriteComponent axeAnimableSpriteComponent = Mappers.animableSprite.get(axe);
//        axeAnimableSpriteComponent.setTileType(agentComponent.getId());
//        axeAnimableSpriteComponent.setSynchronized(true);


//        EquipmentComponent equipment = new EquipmentComponent();
//        equipment.addEquipment(axe);
//        person.add(equipment);


//        Entity person2 = agentService.spawnAgent(new GridPoint2(50, 64), "population");
//        settlement.addPopulation(person2);

//        settlement.addPersonToJob(person);
//        Entity storehouse = settlementService.buildInSettlement("storage/warehouse", new GridPoint2(75, 60));
//        addToStorage(storehouse, "resources/wood_log", 60);
//        addToStorage(storehouse, "resources/stone", 60);


//        settlementService.buildInSettlement("resources/wood_log", new GridPoint2(68, 60), 100);
//        settlementService.buildInSettlement("resources/stone", new GridPoint2(69, 60), 100);
//        itemService.spawnItem(itemService.getItem("resources/stone", 100).build(), new GridPoint2(69, 60));
//        itemService.spawnItem(itemService.getItem("resources/wood_log", 100).build(), new GridPoint2(68, 60));
//        itemService.spawnItem(itemService.getItem("resources/wood_log", 100).build(), new GridPoint2(70, 60));

//        Entity person2 = agentService.spawnAgent(new GridPoint2(33, 35), "population");
//        itemService.spawnItem(itemService.getItem("scenery/big_rock").build(), new GridPoint2(40, 32));
//        settlement.addPopulation(person2);


//        randomSpawn("resources/wood_log", 300, true);
//        randomSpawn("resources/stone", 300, true);
//        randomSpawn("scenery/medium-tree", 150, false);
//        randomSpawn("scenery/big_rock", 30, false);


        //        Entity item = new Entity();
//
//        String name = "test/item";
//        String spriteName = "tools/axe";
//        GameSprite sprite = spriteFactory.getSprite(spriteName);
//
//        item.add(new RenderComponent(sprite, new EquipmentRenderPositionStrategy(), RenderPriority.ITEM))
//            .add(new PositionComponent(32, 32))
//            .add(new StatusComponent(Action.IDLE, Direction.NONE))
//            .add(new SpriteComponent.Builder(spriteName).build())
//            .add(animationComponentFactory.createSpriteStackBuilderComponent())
//            .add(animationComponentFactory.createRefreshSpriteStackBuilderComponent())
//            .add(new AnimableSpriteComponent())
//            .add(new StackedSpritesComponent(animationFactory.get("AXE")))
//            .add(new RefreshSpriteRequirementComponent())
//            .add(new ItemComponent(name, 1))
//            .add(new ProviderComponent()
//            );
//
//        engine.addEntity(item);

    }

    private void exitWorkshop() {
        Entity woodCutter = settlementService.buildInSettlement("providers/wood-cutter", new GridPoint2(30, 32));
        Entity person = agentService.spawnAgent(new GridPoint2(30, 32), "man");

        SettlementComponent settlement = Mappers.settlement.get(selectionService.getSelectedSettlement());
        settlement.addPopulation(person);
        BuildingHelper.spawnInBuilding(person, woodCutter);

        runAfterFrames(20, () -> {
            BuildingComponent building = Mappers.building.get(woodCutter);
            Entity door = building.getDoor();
            DoorComponent doorComponent = Mappers.door.get(door);
            doorComponent.addOccupant(person);
            person.add(new ExitDoorComponent(door));
        });
    }


    private static void setStatus(Entity entity, Action action, Direction direction) {
        StatusComponent status = Mappers.status.get(entity);
        status.setAction(action);
        status.setDirection(Direction.DOWN);
    }

    private void addToStorage(Entity storehouse, String itemName, int quantity) {
        StorageComponent storage = Mappers.storage.get(storehouse);
        Entity entity = itemService.getItem(itemName, quantity).build();
        storage.addItem(itemService.getStorageItem(entity));
    }


    private void addPlayerTown() {
        Entity town = itemService.getItem("settlement/basic_town_1").build();
        itemService.spawnItem(town, new GridPoint2(32, 32));
        selectionService.setSelectedSettlement(town);
        SettlementComponent settlement = Mappers.settlement.get(town);
        settlement.setFreeBuilding(true);

        settlementService.buildInSettlement(town, "houses/house_2", new GridPoint2(31, 33));
        settlementService.buildInSettlement(town, "storage/warehouse", new GridPoint2(36, 33));

        Entity person = agentService.createAgent("population");
        settlement.addPopulation(person);
        settlement.addPersonToHouse(person);
        settlement.addPersonToJob(person);

        settlementService.buildInSettlement(town, "houses/house_1", new GridPoint2(30, 30));
//        settlementService.buildInSettlement(town, "providers/wood_cutter", new GridPoint2(29, 30));
//        Entity tree = itemService.getItem("harvestable/single_tree").build();
//        itemService.spawnItem(tree, new GridPoint2(29, 29));
//
        settlementService.buildInSettlement(town, "providers/farm", new GridPoint2(29, 30));

        person = agentService.createAgent("population");
        settlement.addPopulation(person);
        settlement.addPersonToHouse(person);
        settlement.addPersonToJob(person);

        settlement.setFreeBuilding(false);
    }


    private void randomSpawn(String item, int count) {
        randomSpawn(item, count, false);
    }

    private void randomSpawn(String item, int count, boolean includeHomeArea) {
        for (int i = 0; i < count; i++) {
            GridPoint2 position = new GridPoint2(random.nextInt(high - low) + low, random.nextInt(high - low) + low);
            if (includeHomeArea) {
                itemService.spawnItem(itemService.getItem(item, 4).build(), position);
            } else {
                if (!mapService.isInHomeArea(position)) {
                    itemService.spawnItem(itemService.getItem(item).build(), position);
                }
            }
        }
    }

    public void runAfterFrames(int frames, Runnable action) {
        engine.addSystem(new EntitySystem() {
            int counter = frames;

            @Override
            public void update(float deltaTime) {
                if (counter-- <= 0) {
                    action.run();
                    engine.removeSystem(this);
                }
            }
        });
    }
}
