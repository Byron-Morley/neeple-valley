package com.liquidpixel.main.ai.behavior.utils;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;

import java.util.ArrayList;
import java.util.List;

public class BehaviorTreeState {
    private static class TaskState {
        String taskName;
        Task.Status status;
        List<TaskState> children = new ArrayList<>();

        TaskState(Task<?> task) {
            this.taskName = task.getClass().getSimpleName();
            this.status = task.getStatus();
            for (int i = 0; i < task.getChildCount(); i++) {
                children.add(new TaskState(task.getChild(i)));
            }
        }
    }

    private TaskState rootState;

    public BehaviorTreeState(BehaviorTree<?> tree) {
        this.rootState = new TaskState(tree.getChild(0));
    }

    public void restoreState(BehaviorTree<?> tree) {
        restoreTaskState(tree.getChild(0), rootState);
    }

    private void restoreTaskState(Task<?> task, TaskState state) {
        switch(state.status) {
            case SUCCEEDED: task.success(); break;
            case FAILED: task.fail(); break;
            case RUNNING: task.running(); break;
            default: break;
        }

        for (int i = 0; i < task.getChildCount(); i++) {
            restoreTaskState(task.getChild(i), state.children.get(i));
        }
    }
}
