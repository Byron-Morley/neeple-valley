package com.liquidpixel.main.interfaces.services;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.interfaces.services.ISettlementService;
import com.liquidpixel.main.interfaces.work.IWorkOrder;
import com.liquidpixel.main.results.StorageQueryResult;

import java.util.List;

public interface IStorageService {
    void addItem(StorageComponent storage, IStorageItem storageItem);

    IStorageItem removeItem(StorageComponent storage, IStorageItem storageItem);

    int getAvailableSlots(StorageComponent warehouse);

    boolean hasSpace(StorageComponent storage, IStorageItem storageItem);

    boolean hasAnySpace(StorageComponent storage, IStorageItem storageItem);

    boolean isItemQuantityAvailable(StorageComponent storage, IStorageItem storageItem);

    IStorageItem getFullStack(StorageComponent storage);

    boolean isWorkValid(IWorkOrder workOrder);

    IStorageItem getAnyItemICanCarry(StorageComponent storage);

    int getAvailableQuantity(StorageComponent storage, String itemName);

    StorageQueryResult isThereEnoughSpaceForItem(Entity storageLocation, IStorageItem item, List<IWorkOrder> storageWorkOrders);

    Entity createGroupStorage(GridPoint2 point, ISettlementService settlementService, int width, int height);

    void setPriority(Entity entity, int priority);
}
