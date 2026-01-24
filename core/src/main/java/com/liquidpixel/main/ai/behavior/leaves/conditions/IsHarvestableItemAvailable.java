package com.liquidpixel.main.ai.behavior.leaves.conditions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.liquidpixel.main.ai.blackboards.BehaviorBlackboard;

public class IsHarvestableItemAvailable extends LeafTask<BehaviorBlackboard> {
    @Override
    public Status execute() {

        Gdx.app.debug("DoesWorkshopNeedItemsHauled", "Start");

//        BehaviorBlackboard blackboard = getObject();
//        Entity entity = blackboard.getEntity();
//        IItemService itemService = blackboard.getItemService();
//
//        Entity workshop = blackboard.getWorkshop();
//        StorageComponent workshopStorageComponent = workshop.getComponent(StorageComponent.class);
//        Map<String, StorageItem> requiredResources = itemService.getStorageService().getRequiredResources(workshopStorageComponent);
//
//        if (requiredResources.isEmpty()) {
//            return Status.FAILED;
//        }
//
//        Vector2 agentPosition = Mappers.position.get(entity).getPosition();
//
//        Resources resources = new Resources(requiredResources);
//        Sorter sorter = new Sorter(agentPosition);
//
//        List<Entity> harvestableItems = itemService
//            .getHarvestableItems()
//            .stream()
//            .filter(resources::hasCorrectResources)
//            .sorted(sorter::sortByCloseness)
//            .toList();
//
//        if (harvestableItems.isEmpty()) {
//            return Status.FAILED;
//        }
//
//        for (Entity harvestableItem : harvestableItems) {
//
//            GridPoint2 itemPosition = Mappers.position.get(harvestableItem).getGridPosition();
//            ItemComponent itemComponent = Mappers.item.get(harvestableItem);
//            BodyComponent bodyComponent = Mappers.body.get(harvestableItem);
//            String itemId = itemComponent.getName();
//            GridPoint2 itemInteractionPoint = blackboard.findValidInteractionPoint(reduceToCell(agentPosition), bodyComponent.getInteractionPoint());
//
//            if (itemInteractionPoint != null) {
//
//                blackboard.stack.push(new Value(itemId));
//                blackboard.stack.push(new Value(itemPosition));
//                blackboard.stack.push(new Value(itemInteractionPoint));
//
//                return Status.SUCCEEDED;
//            }
//        }

        return Status.FAILED;
    }

    @Override
    protected Task<BehaviorBlackboard> copyTo(Task<BehaviorBlackboard> task) {
        return task;
    }
}
