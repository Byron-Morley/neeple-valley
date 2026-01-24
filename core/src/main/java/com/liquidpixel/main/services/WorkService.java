package com.liquidpixel.main.services;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.ai.actions.WorkTaskComponent;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.IRecipe;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.interfaces.work.IWorkService;

import java.util.List;

public class WorkService extends Service implements IWorkService {
    IItemService itemService;

    public WorkService(IItemService itemService) {
        this.itemService = itemService;
    }

    public void createOutput(WorkTaskComponent workTaskComponent, Entity workshop) {
        List<IStorageItem> output = workTaskComponent.getRecipe().getOutput();
        for (IStorageItem item : output) {
            Entity itemEntity = itemService.getItem(item.getName(), item.getQuantity()).build();
            itemService.spawnItem(itemEntity);
//            itemService.getStorageService().storeItem(itemEntity, workshop.getComponent(BodyComponent.class).getInteractionPoint());
//            itemEntity.add(new PickupableComponent());
        }
    }

    public void removeInput(StorageComponent workshopStorageComponent, WorkTaskComponent workTaskComponent) {

//        Map<String, Entity> storageItems = workshopStorageComponent.getItems();
//        List<IStorageItem> input = workTaskComponent.getRecipe().getInput();
//
//        for (IStorageItem inputItem : input) {
//            Entity itemEntity = storageItems.get(inputItem.getName());
//            if (itemEntity != null) {
//                ItemComponent itemComponent = Mappers.item.get(itemEntity);
//                int totalRemaining = itemComponent.getQuantity() - inputItem.getQuantity();
//                if (totalRemaining <= 0) {
//                    workshopStorageComponent.removeItem(inputItem.getName());
//                    itemService.dispose(itemEntity);
//                } else {
//                    workshopStorageComponent.updateUI();
//                    itemComponent.setQuantity(totalRemaining);
//                }
//            }
//        }
    }

    public void buildItem(WorkTaskComponent workTaskComponent, Entity foundation) {

        IRecipe recipe = workTaskComponent.getRecipe();
        IStorageItem building = recipe.getOutput().get(0);
        PositionComponent position = foundation.getComponent(PositionComponent.class);
        StorageComponent storageComponent = foundation.getComponent(StorageComponent.class);
        removeInput(storageComponent, workTaskComponent);
        itemService.dispose(foundation);
        Entity newBuilding = itemService.getItem(building.getName()).build();
        itemService.spawnItem(newBuilding, position.getGridPosition());
    }
}
