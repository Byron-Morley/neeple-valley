package com.liquidpixel.main.ai.behavior.leaves.conditions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.liquidpixel.main.ai.blackboards.BehaviorBlackboard;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.interfaces.services.IItemService;

import java.util.List;

public class CanIBuildSomething extends LeafTask<BehaviorBlackboard> {
    @Override
    public Status execute() {
        Gdx.app.debug("CanIBuildSomething", "Start");

        BehaviorBlackboard blackboard = getObject();
        IItemService itemService = blackboard.getItemService();
        List<Entity> buildableItems = itemService.getBuildableItems();

        if (buildableItems.isEmpty()) {
            return Status.FAILED;
        }

        for (Entity foundation : buildableItems) {
            StorageComponent storageComponent = foundation.getComponent(StorageComponent.class);
//            if (storageComponent != null &&
//                itemService.getStorageService().getListOfRequiredResources(storageComponent) == null) {
//                blackboard.stack.push(new Value(foundation));
//                return Status.SUCCEEDED;
//            }
        }
        return Status.FAILED;
    }

    @Override
    protected Task<BehaviorBlackboard> copyTo(Task<BehaviorBlackboard> task) {
        return task;
    }
}
