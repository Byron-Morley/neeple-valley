package com.liquidpixel.main.ai.tasks.composite;

import com.badlogic.gdx.ai.btree.Task;
import com.liquidpixel.main.ai.blackboards.Blackboard;
import com.liquidpixel.main.ai.tasks.composite.CompositeQueueTask;

public class TaskRunner<T extends Blackboard> extends CompositeQueueTask<T> {

    @Override
    public void run() {
        if (!queue.isEmpty()) {
            Task<T> currentTask = queue.remove();
            try {
                currentTask.getObject();
            } catch (IllegalStateException e) {
                currentTask.setControl(control);
            }
            currentTask.run();
        }
    }

    @Override
    public void childSuccess(Task<T> task) {
        if (!queue.isEmpty()) {
            run();
        } else {
            active = false;
        }
    }

    @Override
    public void childFail(Task<T> task) {
        fail();
    }

    @Override
    public void childRunning(Task<T> task, Task<T> task1) {

    }
}
