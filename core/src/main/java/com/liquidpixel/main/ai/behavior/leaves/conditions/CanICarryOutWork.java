package com.liquidpixel.main.ai.behavior.leaves.conditions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.liquidpixel.main.ai.blackboards.BehaviorBlackboard;
import com.liquidpixel.main.components.storage.StorageComponent;

public class CanICarryOutWork extends LeafTask<BehaviorBlackboard> {
    @Override
    public Status execute() {
        Gdx.app.debug("CanICarryOutWork", "start");
        BehaviorBlackboard blackboard = getObject();
        Entity workshop = blackboard.getWorkshop();
        StorageComponent workshopStorageComponent = workshop.getComponent(StorageComponent.class);
//        IStorageService storageService = blackboard.getItemService().getStorageService();

//        if (workshopStorageComponent != null && workshopStorageComponent.getRecipes() == null) {
//            return Status.FAILED;
//        }

//      only 1 recipe is needed, when you find it you select it
//        IRecipe recipe = storageService.getWorkableRecipe(workshopStorageComponent);
//        if (recipe != null) return Status.SUCCEEDED;

        return Status.FAILED;
    }

    @Override
    protected Task<BehaviorBlackboard> copyTo(Task<BehaviorBlackboard> task) {
        return task;
    }
}
