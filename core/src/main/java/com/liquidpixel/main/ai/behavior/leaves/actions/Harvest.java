package com.liquidpixel.main.ai.behavior.leaves.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.ai.blackboards.BehaviorBlackboard;
import com.liquidpixel.main.components.ai.actions.HarvestComponent;
import com.liquidpixel.main.model.status.WorkState;

public class Harvest extends LeafTask<BehaviorBlackboard> {
    @Override
    public Status execute() {
        BehaviorBlackboard blackboard = getObject();
        Entity agent = blackboard.getEntity();

        HarvestComponent harvest = agent.getComponent(HarvestComponent.class);
        if (harvest == null) {
            GridPoint2 interactionPoint = blackboard.stack.pop().getPosition();
            GridPoint2 location = blackboard.stack.pop().getPosition();
            String itemName = blackboard.stack.pop().getString();

            harvest = new HarvestComponent(itemName, location, interactionPoint);
            agent.add(harvest);
            return Status.RUNNING;
        }

        if (harvest.state == WorkState.COMPLETED) {
            agent.remove(HarvestComponent.class);
            return Status.SUCCEEDED;
        }

        if (harvest.state == WorkState.FAILED) {
            agent.remove(HarvestComponent.class);
            return Status.FAILED;
        }

        return Status.RUNNING;
    }

    @Override
    protected Task<BehaviorBlackboard> copyTo(Task<BehaviorBlackboard> task) {
        return task;
    }
}
