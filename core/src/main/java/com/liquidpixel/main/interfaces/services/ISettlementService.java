package com.liquidpixel.main.interfaces.services;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.interfaces.IRecipe;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.interfaces.work.IWorkOrder;
import com.liquidpixel.main.model.storage.WorkOrder;
import com.liquidpixel.main.services.SettlementService;

import java.util.List;

public interface ISettlementService {

    Entity buildInSettlement(String name, GridPoint2 position);

    Entity buildInSettlement(String name, GridPoint2 position, int quantity);

    Entity buildInSettlement(Entity settlement, String name, GridPoint2 position);

    Entity buildFoundationInSettlement(String name, GridPoint2 position);

    Entity buildFoundationInSettlement(Entity settlement, String name, GridPoint2 position);

    void payForRecipe(IRecipe recipe);

    void payForRecipe(Entity settlement, IRecipe recipe);

    boolean canIAfford(String recipeName);

    boolean canIAfford(Entity settlement, String recipeName);

    Entity getSelectedSettlement();

    void setSelectedSettlement(Entity selectedSettlement);

    int getResourceQuantity(String name);

    int getResourceQuantity(Entity settlement, String name);

    List<IStorageItem> getSettlementResources();

    List<IStorageItem> getSettlementResources(Entity settlement);

    int getQuantityCurrentlyInWorkOrders(Entity queryEntity, SettlementService.QueryType queryType, String itemName);

    int getQuantityCurrentlyInWorkOrders(Entity settlementEntity, Entity queryEntity, SettlementService.QueryType queryType, String itemName);
    List<IWorkOrder> getWorkOrdersByState(WorkOrder.State state);
}
