package com.liquidpixel.main.ai.behavior.leaves.conditions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.liquidpixel.main.ai.blackboards.BehaviorBlackboard;
import com.liquidpixel.main.components.storage.StorageComponent;

public class DoesWorkshopNeedItemsHauled extends LeafTask<BehaviorBlackboard> {

    @Override
    public Status execute() {
        Gdx.app.debug("DoesWorkshopNeedItemsHauled", "Start");
        BehaviorBlackboard blackboard = getObject();
        Entity workshop = blackboard.getWorkshop();
        StorageComponent workshopStorageComponent = workshop.getComponent(StorageComponent.class);
//        Map<String, StorageItem> requiredResources = blackboard.getItemService().getStorageService().getListOfRequiredResources(workshopStorageComponent);
//
//        if (requiredResources == null) {
//            Gdx.app.debug("DoesWorkshopNeedItemsHauled", "requiredResources is null");
//            return Status.FAILED;
//        }
//
//        List<Entity> entities = blackboard.getListOfPotentialEntitiesToPickup();
//        if (entities.isEmpty()){
//            return Status.FAILED;
//        }
//
//        BehaviorBlackboard.Haul haul = blackboard.getSuitableEntityToHaul(entities, requiredResources);
//
//        if (haul == null){
//            return Status.FAILED;
//        }
//
//        BodyComponent bodyComponent = Mappers.body.get(workshop);
//
//        blackboard.stack.push(new Value(haul.getItemName()));
//        blackboard.stack.push(new Value(Mappers.position.get(haul.getEntity()).getGridPosition()));
//        blackboard.stack.push(new Value(bodyComponent.getInteractionPoint()));

        return Status.SUCCEEDED;
    }


    @Override
    protected Task<BehaviorBlackboard> copyTo(Task<BehaviorBlackboard> task) {
        return task;
    }

}
