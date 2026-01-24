package com.liquidpixel.main.interfaces.managers;

import com.liquidpixel.main.ai.blackboards.TaskBlackboard;
import com.liquidpixel.main.ai.tasks.composite.TaskRunner;

public interface ITaskManager {
    TaskRunner<TaskBlackboard> getTaskRunner();
}
