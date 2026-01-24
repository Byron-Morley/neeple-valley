package com.liquidpixel.main.ai.behavior.leaves.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.liquidpixel.main.ai.blackboards.BehaviorBlackboard;
import com.liquidpixel.main.components.ai.actions.DoWorkComponent;
import com.liquidpixel.main.components.ai.actions.WorkTaskComponent;
public class Build extends LeafTask<BehaviorBlackboard> {
    @Override
    public Status execute() {
        Gdx.app.debug("Work", "Building");
        BehaviorBlackboard blackboard = getObject();
        Entity agent = blackboard.getEntity();

        DoWorkComponent work = agent.getComponent(DoWorkComponent.class);
        if (work == null) {
            Entity workshop = blackboard.stack.pop().getEntity();
            work = new DoWorkComponent(workshop);
            agent.add(work);
            return Status.RUNNING;
        }

        // Check states and handle completion
        if (work.state == DoWorkComponent.State.COMPLETED) {
            WorkTaskComponent workTask = agent.getComponent(WorkTaskComponent.class);
            blackboard.getItemService().getWorkService().buildItem(workTask, work.getWorkshop());
            agent.remove(WorkTaskComponent.class);
            agent.remove(DoWorkComponent.class);
            return Status.SUCCEEDED;
        }

        if (work.state == DoWorkComponent.State.FAILED) {
            agent.remove(DoWorkComponent.class);
            return Status.FAILED;
        }

        return Status.RUNNING;
    }

    @Override
    protected Task<BehaviorBlackboard> copyTo(Task<BehaviorBlackboard> task) {
        return task;
    }
}
