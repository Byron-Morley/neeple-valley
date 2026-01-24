package com.liquidpixel.main.ai.behavior.leaves.conditions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.liquidpixel.main.ai.blackboards.BehaviorBlackboard;
import com.liquidpixel.main.interfaces.services.IItemService;

import java.util.List;

public class DoesFoundationNeedResources extends LeafTask<BehaviorBlackboard> {
    @Override
    public Status execute() {
        Gdx.app.debug("DoesFoundationNeedResources", "Start");

        BehaviorBlackboard blackboard = getObject();
        IItemService itemService = blackboard.getItemService();
        List<Entity> buildableItems = itemService.getBuildableItems();

        if (buildableItems.isEmpty()) {
            return Status.FAILED;
        }

//        for (Entity workshop : buildableItems) {
//            StorageComponent workshopStorageComponent = workshop.getComponent(StorageComponent.class);
//            Map<String, StorageItem> requiredResources = itemService.getStorageService().getListOfRequiredResources(workshopStorageComponent);
//            if (requiredResources != null) {
//
//                List<Entity> entities = blackboard.getListOfPotentialEntitiesToPickup();
//                if (!entities.isEmpty()) {
//                    BehaviorBlackboard.Haul haul = blackboard.getSuitableEntityToHaul(entities, requiredResources);
//
//                    if (haul != null) {
//
//                        BodyComponent bodyComponent = Mappers.body.get(workshop);
//
//                        blackboard.stack.push(new Value(haul.getItemName()));
//                        blackboard.stack.push(new Value(Mappers.position.get(haul.getEntity()).getGridPosition()));
//                        blackboard.stack.push(new Value(bodyComponent.getInteractionPoint()));
//                        return Status.SUCCEEDED;
//                    }
//                }
//            }
//        }

        return Status.FAILED;
    }

    @Override
    protected Task<BehaviorBlackboard> copyTo(Task<BehaviorBlackboard> task) {
        return task;
    }
}
