package com.liquidpixel.main.ai.tasks.composite;


import com.badlogic.gdx.ai.btree.BranchTask;
import com.badlogic.gdx.ai.btree.Task;
import com.liquidpixel.main.ai.blackboards.Blackboard;

import java.util.LinkedList;
import java.util.Queue;

public abstract class CompositeQueueTask<T extends Blackboard> extends BranchTask<T> {
    protected Queue<Task> queue;
    protected boolean active;

    public CompositeQueueTask() {
        queue = new LinkedList<Task>();
        active = false;
    }

    public void addToQueue(Task task) {
        queue.add(task);
        if (!active) {
            start();
            active = true;
        }
    }

    public int getQueueSize() {
        return queue.size();
    }
}
