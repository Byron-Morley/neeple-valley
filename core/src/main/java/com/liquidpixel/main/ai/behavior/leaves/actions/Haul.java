package com.liquidpixel.main.ai.behavior.leaves.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.ai.blackboards.BehaviorBlackboard;
import com.liquidpixel.main.components.ai.actions.HaulComponent;
import com.liquidpixel.main.model.status.WorkState;

public class Haul extends LeafTask<BehaviorBlackboard> {

    @Override
    public Status execute() {
        BehaviorBlackboard blackboard = getObject();
        Entity agent = blackboard.getEntity();
//
//        HaulComponent haul = agent.getComponent(HaulComponent.class);
//        if (haul == null) {
//            GridPoint2 dropLocation = blackboard.stack.pop().getPosition();
//            GridPoint2 pickupLocation = blackboard.stack.pop().getPosition();
//            String itemName = blackboard.stack.pop().getString();
//
//            haul = new HaulComponent(itemName, pickupLocation, dropLocation);
//            agent.add(haul);
//            return Status.RUNNING;
//        }
//
//        if (haul.state == WorkState.COMPLETED) {
//            agent.remove(HaulComponent.class);
//            return Status.SUCCEEDED;
//        }
//
//        if (haul.state == WorkState.FAILED) {
//            if (haul.getInaccessiblePosition() != null) {
//                blackboard.getBlackList().add(haul.getInaccessiblePosition());
//            }
//            agent.remove(HaulComponent.class);
//            return Status.FAILED;
//        }

        return Status.RUNNING;
    }

    @Override
    protected Task<BehaviorBlackboard> copyTo(Task<BehaviorBlackboard> task) {
        return task;
    }
}
