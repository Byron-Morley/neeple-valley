package com.liquidpixel.main.managers.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.core.components.core.StatusComponent;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.main.managers.core.GameManager;
import com.liquidpixel.core.core.Action;
import com.liquidpixel.core.core.Direction;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.pathfinding.api.IMapService;

import java.util.*;

import static com.liquidpixel.main.screens.WorldScreen.WORLD_WIDTH;

public class WorldLevelManager extends GameManager {
    IMapService mapService;
    IWorldMap worldMap;
    ISelectionService selectionService;
    ISettlementService settlementService;
    Random random;
    int high = WORLD_WIDTH;
    int low = 1;
    Engine engine;

    public WorldLevelManager(
        IAgentService agentService,
        IItemService itemService,
        IMapService mapService,
        ISelectionService selectionService,
        ISettlementService settlementService
    ) {
        super(agentService, itemService);
        this.settlementService = settlementService;
        this.selectionService = selectionService;
        this.mapService = mapService;
        worldMap = mapService.getWorldMap();
        this.random = new Random();
        engine = GameResources.get().getEngine();
    }

    public void init() {

        System.out.println("World Level Manager Initialized");

        Entity agent = agentService.spawnAgent(new GridPoint2(11, 11), "man");
        Entity item = itemService.getItem("scenery/medium-water-rock").build();
        itemService.spawnItem(item, new GridPoint2(10, 10));

        System.out.println("World Level Manager Initialized - Agent positioned to water");
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


    }


    private static void setStatus(Entity entity, Action action, Direction direction) {
        StatusComponent status = Mappers.status.get(entity);
        status.setAction(action);
        status.setDirection(Direction.DOWN);
    }

    private void addToStorage(Entity storehouse, String itemName, int quantity) {
        StorageComponent storage = Mappers.storage.get(storehouse);
        Entity entity = itemService.getItem(itemName, quantity).build();
        ItemComponent stoneItem = Mappers.item.get(entity);
        storage.addItem(stoneItem.getItem());
    }


    private void addPlayerTown() {
        Entity town = itemService.getItem("settlement/basic_town_1").build();
        itemService.spawnItem(town, new GridPoint2(32, 32));
        selectionService.setSelectedSettlement(town);
        SettlementComponent settlement = Mappers.settlement.get(town);
        settlement.setFreeBuilding(true);

        settlementService.buildInSettlement(town, "houses/house_2", new GridPoint2(31, 33));
        settlementService.buildInSettlement(town, "storage/warehouse", new GridPoint2(36, 33));

        Entity person = agentService.getAgent("population");
        settlement.addPopulation(person);
        settlement.addPersonToHouse(person);
        settlement.addPersonToJob(person);

        settlementService.buildInSettlement(town, "houses/house_1", new GridPoint2(30, 30));
//        settlementService.buildInSettlement(town, "providers/wood_cutter", new GridPoint2(29, 30));
//        Entity tree = itemService.getItem("harvestable/single_tree").build();
//        itemService.spawnItem(tree, new GridPoint2(29, 29));
//
        settlementService.buildInSettlement(town, "providers/farm", new GridPoint2(29, 30));

        person = agentService.getAgent("population");
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
}
