package com.liquidpixel.main.ai.blackboards;


import com.liquidpixel.main.ai.tasks.PoolableTaskManager;

public abstract class Blackboard {
    public Blackboard() {}
    public abstract PoolableTaskManager getDomainTaskManager();
}
