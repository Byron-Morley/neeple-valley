package com.liquidpixel.main.scenarios;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.factories.ModelFactory;
import com.liquidpixel.main.interfaces.IScenario;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.ScenarioState;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.main.model.item.Item;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.main.services.items.StorageHelper;
import com.liquidpixel.sprite.model.GameSprite;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.pathfinding.api.IMapService;

import java.util.Arrays;
import java.util.List;

public class WoodCutterScenario extends Scenario implements IScenario {

    public WoodCutterScenario(IMapService mapService, IWorldMap worldMap, ISelectionService selectionService, ISettlementService settlementService, IAgentService agentService, IItemService itemService) {
        super(mapService, worldMap, selectionService, settlementService, agentService, itemService);
    }

    @Override
    public void start() {
        Entity person = agentService.spawnAgent(new GridPoint2(12, 12), "population");
        trackEntity(person);

        Entity building = itemService.getItem("providers/wood-cutter").build();
        itemService.spawnItem(building, new GridPoint2(13, 13));
        trackEntity(building);
    }

    @Override
    public void reset() {
        clearAllSpawnedEntities();
    }

    @Override
    public List<ScenarioState> getAvailableStates() {
        return Arrays.asList(
            new ScenarioState("state1", "State 1 - Pre-fill Settlement Resources"),
            new ScenarioState("state2", "State 2 - Building + Population"),
            new ScenarioState("state3", "State 3 - Building + Population + Wood to Floor"),
            new ScenarioState("state4", "State 4 - Building + Population + Wood to Floor Storage"),
            new ScenarioState("state5", "State 5 - Building + Population + Wood to Storehouse"),
            new ScenarioState("state6", "State 6 - Building + Population + Storehouse")
        );
    }

    @Override
    public boolean loadState(String stateId) {
        reset();

        switch (stateId) {
            case "state1":
                loadState1();
                return true;
            case "state2":
                loadState2();
                return true;
            case "state3":
                loadState3();
                return true;
            case "state4":
                loadState4();
                return true;
            case "state5":
                loadState5();
                return true;
            case "state6":
                loadState6();
                return true;
            default:
                System.out.println("Unknown state: " + stateId);
                return false;
        }
    }

    private void loadState1() {
        // Create settlement and pre-fill with resources to build wood cutter
        Entity settlement = createSettlement();
        preloadSettlementResources(settlement);
    }

    private void loadState2() {
        // Add building and population to settlement
        Entity settlement = createSettlement();
        Entity woodCutter = addWoodCutterToSettlement(settlement);
        addPopulationToSettlement(settlement);
    }

    private void loadState3() {
        // Add building, population, and wood to floor
        Entity settlement = createSettlement();
        Entity woodCutter = addWoodCutterToSettlement(settlement);
        addPopulationToSettlement(settlement);
        addWoodToFloor(new GridPoint2(14, 14));
    }

    private void loadState4() {
        // Add building, population, and wood to floor storage
        Entity settlement = createSettlement();
        Entity woodCutter = addWoodCutterToSettlement(settlement);
        addPopulationToSettlement(settlement);
        Entity floorStorage = addFloorStorage(new GridPoint2(14, 14));
        addWoodToStorage(floorStorage);
    }

    private void loadState5() {
        // Add building, population, and wood to storehouse storage
        Entity settlement = createSettlement();
        Entity woodCutter = addWoodCutterToSettlement(settlement);
        addPopulationToSettlement(settlement);
        Entity storehouse = addStorehouseToSettlement(settlement);
        addWoodToStorage(storehouse);
    }

    private void loadState6() {
        // Add building, population, and storehouse
        Entity settlement = createSettlement();
        Entity woodCutter = addWoodCutterToSettlement(settlement);
        addPopulationToSettlement(settlement);
        Entity storehouse = addStorehouseToSettlement(settlement);
    }

    private Entity createSettlement() {
        Entity settlement = itemService.getItem("settlement/basic_town_1").build();
        itemService.spawnItem(settlement, new GridPoint2(10, 10));
        trackEntity(settlement);

        SettlementComponent settlementComponent = Mappers.settlement.get(settlement);
        settlementComponent.setFreeBuilding(true);
        selectionService.setSelectedSettlement(settlement);

        return settlement;
    }

    private void preloadSettlementResources(Entity settlement) {
        // Add enough resources to build a wood cutter
        SettlementComponent settlementComponent = Mappers.settlement.get(settlement);

        // Add basic building materials (wood, stone, etc.)
        addResourceToSettlement(settlement, "wood", 20);
        addResourceToSettlement(settlement, "stone", 10);
        addResourceToSettlement(settlement, "planks", 5);
    }

    private Entity addWoodCutterToSettlement(Entity settlement) {
        Entity woodCutter = settlementService.buildInSettlement(settlement, "providers/wood-cutter", new GridPoint2(13, 13));
        trackEntity(woodCutter);
        return woodCutter;
    }

    private void addPopulationToSettlement(Entity settlement) {
        SettlementComponent settlementComponent = Mappers.settlement.get(settlement);

        // Add house for population
        Entity house = settlementService.buildInSettlement(settlement, "houses/house_1", new GridPoint2(11, 11));
        trackEntity(house);

        // Add population
        Entity person = agentService.createAgent("population");
        settlementComponent.addPopulation(person);
        settlementComponent.addPersonToHouse(person);
        settlementComponent.addPersonToJob(person);
        trackEntity(person);
    }

    private void addWoodToFloor(GridPoint2 position) {
        // Create wood item on the floor
        Entity woodItem = createWoodItem(10);
        itemService.spawnItem(woodItem, position);
        trackEntity(woodItem);
    }

    private Entity addFloorStorage(GridPoint2 position) {
        // Create floor storage entity
        Entity floorStorage = itemService.getItem("storage/floor_storage").build();
        itemService.spawnItem(floorStorage, position);
        trackEntity(floorStorage);

        return floorStorage;
    }

    private Entity addStorehouseToSettlement(Entity settlement) {
        Entity storehouse = settlementService.buildInSettlement(settlement, "storage/storehouse", new GridPoint2(15, 15));
        trackEntity(storehouse);
        return storehouse;
    }

    private void addWoodToStorage(Entity storageEntity) {
        StorageComponent storage = Mappers.storage.get(storageEntity);
        if (storage != null) {
            StorageItem woodItem = createWoodStorageItem(15);
            StorageHelper.addItem(storage, woodItem);
        }
    }

    private void addResourceToSettlement(Entity settlement, String resourceName, int quantity) {
        SettlementComponent settlementComponent = Mappers.settlement.get(settlement);

        // Create a temporary storage building if none exists
        Entity tempStorage = itemService.getItem("storage/temp_storage").build();
        itemService.spawnItem(tempStorage, new GridPoint2(9, 9));
        trackEntity(tempStorage);

        // Add the resource to the storage
        StorageComponent storage = Mappers.storage.get(tempStorage);
        if (storage != null) {
            StorageItem resourceItem = createResourceStorageItem(resourceName, quantity);
            StorageHelper.addItem(storage, resourceItem);
        }

        // Add to settlement as asset
        settlementComponent.addAsset(tempStorage, settlement);
    }

    private Entity createWoodItem(int quantity) {
        Entity woodItem = itemService.getItem("resources/wood", quantity).build();
        return woodItem;
    }

    private StorageItem createWoodStorageItem(int quantity) {
        return createResourceStorageItem("wood", quantity);
    }

    private StorageItem createResourceStorageItem(String resourceName, int quantity) {
        try {
            Item item = ModelFactory.getItemsModel().get("resources/" + resourceName);
            GameSprite sprite = null;
            return new StorageItem(resourceName, quantity, item.getStackSize(), sprite);
        } catch (Exception e) {
            // Fallback to default values if model not found
            GameSprite sprite = null;
            return new StorageItem(resourceName, quantity, 64, sprite);
        }
    }
}
