package com.liquidpixel.main.ai.blackboards;

import com.liquidpixel.main.ai.blackboards.Blackboard;
import com.liquidpixel.main.ai.tasks.PoolableTaskManager;
import com.liquidpixel.main.ai.tasks.TaskManager;

public class TaskBlackboard extends Blackboard {

    TaskManager taskManager;

    public TaskBlackboard(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public PoolableTaskManager getDomainTaskManager() {
        return taskManager;
    }
}
