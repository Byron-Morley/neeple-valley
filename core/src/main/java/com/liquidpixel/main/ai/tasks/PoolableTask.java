package com.liquidpixel.main.ai.tasks;


import com.badlogic.gdx.ai.btree.LeafTask;
import com.liquidpixel.main.ai.blackboards.Blackboard;
import com.liquidpixel.main.ai.blackboards.TaskBlackboard;

public abstract class PoolableTask<T extends Blackboard> extends LeafTask<TaskBlackboard> {

    @Override
    public Status execute() {
        TaskBlackboard blackboard = getObject();
        blackboard.getDomainTaskManager().addPoolableTask(this);
        return Status.SUCCEEDED;
    }

    public abstract void tick(float delta);

    public void end() {
        TaskBlackboard blackboard = getObject();
        blackboard.getDomainTaskManager().removePoolableTask(this);
    }
}
