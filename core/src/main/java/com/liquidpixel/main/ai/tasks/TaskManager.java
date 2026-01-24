package com.liquidpixel.main.ai.tasks;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.liquidpixel.main.ai.blackboards.TaskBlackboard;
import com.liquidpixel.main.ai.tasks.PoolableTaskManager;
import com.liquidpixel.main.ai.tasks.composite.TaskRunner;
import com.liquidpixel.main.interfaces.managers.ITaskManager;

public class TaskManager extends PoolableTaskManager implements ITaskManager {

    Screen screen;
    TaskBlackboard taskBlackboard;
    BehaviorTree<TaskBlackboard> tree;
    TaskRunner<TaskBlackboard> taskRunner;

    public TaskManager(Screen screen) {
        this.screen = screen;
        taskRunner = new TaskRunner();
        taskBlackboard = new TaskBlackboard(this);
        tree = new BehaviorTree<>(taskRunner);
        tree.setObject(taskBlackboard);
    }

    public void tick(float delta) {
        super.tick(delta);
        if (taskRunner.getQueueSize() > 0) {
            tree.step();
        }
    }

    public TaskRunner<TaskBlackboard> getTaskRunner() {
        return taskRunner;
    }
}
