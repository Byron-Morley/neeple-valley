package com.liquidpixel.main.services;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.AssetComponent;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.factories.ModelFactory;
import com.liquidpixel.main.interfaces.*;
import com.liquidpixel.main.interfaces.managers.ISelectionManager;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.services.ISettlementService;
import com.liquidpixel.main.interfaces.work.IWorkOrder;
import com.liquidpixel.main.model.item.Item;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.main.model.storage.WorkOrder;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SettlementService implements ISettlementService {

    public enum QueryType {
        ORIGIN,
        DESTINATION,
    }

    ISelectionManager selectionManager;
    IItemService itemService;
    ISpriteFactory spriteFactory;


    public SettlementService(ISelectionManager selectionManager, IItemService itemService, ISpriteFactory spriteFactory) {
        this.selectionManager = selectionManager;
        this.itemService = itemService;
        this.spriteFactory = spriteFactory;
    }

    @Override
    public Entity buildInSettlement(String name, GridPoint2 position) {
        return this.buildInSettlement(getSelectedSettlement(), name, position);
    }

    @Override
    public Entity buildInSettlement(String name, GridPoint2 position, int quantity) {
        return this.buildInSettlement(getSelectedSettlement(), name, position, quantity);
    }

    @Override
    public Entity buildInSettlement(Entity settlement, String name, GridPoint2 position) {
        return this.buildInSettlement(settlement, name, position, 1);
    }

    public Entity buildInSettlement(Entity settlement, String name, GridPoint2 position, int quantity) {
        Entity building = itemService.getItem(name, quantity).build();
        itemService.spawnItem(building, position);
        building.add(new AssetComponent(settlement));
        SettlementComponent settlementComponent = Mappers.settlement.get(settlement);
        settlementComponent.addAsset(building, settlement);

        return building;
    }

    @Override
    public Entity buildFoundationInSettlement(String name, GridPoint2 position) {
        return this.buildFoundationInSettlement(getSelectedSettlement(), name, position);
    }

    @Override
    public Entity buildFoundationInSettlement(Entity settlement, String name, GridPoint2 position) {
        Entity foundation = itemService.getFoundationItem(name).build();

        itemService.spawnItem(foundation, position);
        foundation.add(new AssetComponent(settlement));
        SettlementComponent settlementComponent = Mappers.settlement.get(settlement);
        settlementComponent.addAsset(foundation, settlement);

        return foundation;
    }

    @Override
    public void payForRecipe(IRecipe recipe) {
        this.payForRecipe(getSelectedSettlement(), recipe);
    }

    @Override
    public void payForRecipe(Entity settlement, IRecipe recipe) {
        SettlementComponent settlementComponent = Mappers.settlement.get(settlement);


        //TODO we need to work out how to pay for buildings

        for (IStorageItem item : recipe.getInput()) {
//            settlementComponent.removeFromStorage(item.getName(), item.getQuantity());
        }
    }

    public int getResourceQuantity(String name) {
        return this.getResourceQuantity(getSelectedSettlement(), name);
    }

    @Override
    public int getResourceQuantity(Entity settlementEntity, String name) {
        try {
            return getResource(settlementEntity, name).getQuantity();
        } catch (Exception e) {
            return 0;
        }
    }

    private IStorageItem getResource(Entity settlementEntity, String name) throws Exception {
        if (settlementEntity == null) {
            throw new Exception("Settlement is null");
        }

        int total = 0;
        SettlementComponent settlement = Mappers.settlement.get(settlementEntity);

        for (Entity building : settlement.getAssets()) {
            StorageComponent storageComponent = Mappers.storage.get(building);
            if (Mappers.resource.has(building)) {
                ItemComponent itemComponent = Mappers.item.get(building);
                if (itemComponent.getName().equals(name)) {
                    total += itemComponent.getQuantity();
                }
            } else if (storageComponent != null) {
                IStorageItem storage = Mappers.storage.get(building).getItems().get(name);
                if (storage != null) {
                    total += storage.getQuantity();
                }
            }
        }
        Item item = ModelFactory.getItemsModel().get(name);

        return new StorageItem(item.getName(), total, item.getStackSize(), spriteFactory.getSprite(item.getSpriteName()));
    }

    @Override
    public boolean canIAfford(String name) {
        return this.canIAfford(getSelectedSettlement(), name);
    }


    @Override
    public boolean canIAfford(Entity settlement, String name) {
        try {
            IRecipe recipe = itemService.getRecipe(name);
            boolean canAfford = true;
            for (IStorageItem item : recipe.getInput()) {
                if (getResourceQuantity(settlement, item.getName()) < item.getQuantity()) {
                    canAfford = false;
                    break;
                }
            }
            return canAfford;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Entity getSelectedSettlement() {
        return selectionManager.getSelectedSettlement();
    }

    @Override
    public void setSelectedSettlement(Entity selectedSettlement) {
        selectionManager.setSelectedSettlement(selectedSettlement);
    }


    public List<IStorageItem> getSettlementResources() {
        return getSettlementResources(getSelectedSettlement());
    }

    public List<IStorageItem> getSettlementResources(Entity settlementEntity) {
        List<IStorageItem> resources = new ArrayList<>();

        if (settlementEntity == null) {
            return resources;
        }

        SettlementComponent settlement = Mappers.settlement.get(settlementEntity);

        for (Entity building : settlement.getAssets()) {
            StorageComponent storageComponent = Mappers.storage.get(building);
            if (Mappers.resource.has(building)) {
                calculateResourceTotals(resources, itemService.getStorageItem(building));
            } else if (storageComponent != null) {
                for (Map.Entry<String, IStorageItem> entry : storageComponent.getItems().entrySet()) {
                    calculateResourceTotals(resources, entry.getValue());
                }
            }
        }
        return resources;
    }

    private void calculateResourceTotals(List<IStorageItem> resources, IStorageItem currentItem) {
        // Check if we already have this item in our resources list
        boolean found = false;
        for (IStorageItem resource : resources) {
            if (resource.getName().equals(currentItem.getName())) {
                // Item already in list, just update quantity
                resource.setQuantity(resource.getQuantity() + currentItem.getQuantity());
                found = true;
                break;
            }
        }

        // If not found, create a new StorageItem and add it to the list
        if (!found) {
            // Create a new StorageItem to avoid reference issues
            resources.add(new StorageItem(
                currentItem.getName(),
                currentItem.getQuantity(),
                currentItem.getStackSize(),
                currentItem.getSprite()
            ));
        }
    }


    public int getQuantityCurrentlyInWorkOrders(Entity queryEntity, QueryType queryType, String itemName) {
        return getQuantityCurrentlyInWorkOrders(getSelectedSettlement(), queryEntity, queryType, itemName);
    }

    public int getQuantityCurrentlyInWorkOrders(Entity settlementEntity, Entity queryEntity, QueryType queryType, String itemName) {
        SettlementComponent settlement = Mappers.settlement.get(settlementEntity);

        switch (queryType) {
            case ORIGIN:
                return settlement.getWorkOrders()
                    .stream()
                    .filter(work -> work.getOrigin() == queryEntity)
                    .filter(work -> work.getItemName().equals(itemName))
                    .filter(IWorkOrder::isActive)
                    .mapToInt(IWorkOrder::getQuantity)
                    .sum();
            case DESTINATION:
                return settlement.getWorkOrders()
                    .stream()
                    .filter(work -> work.getDestination() == queryEntity)
                    .filter(work -> work.getItemName().equals(itemName))
                    .filter(IWorkOrder::isActive)
                    .mapToInt(IWorkOrder::getQuantity)
                    .sum();
            default:
                return 0;
        }
    }

    public List<IWorkOrder> getWorkOrdersByState(WorkOrder.State state) {

        SettlementComponent settlement = null;
        try {
            settlement = Mappers.settlement.get(getSelectedSettlement());
        } catch (Exception e) {
            return new ArrayList<>();
        }
        return settlement.getWorkOrders()
            .stream()
            .filter(order -> order.getState() == state)
            .collect(Collectors.toList());
    }

}
