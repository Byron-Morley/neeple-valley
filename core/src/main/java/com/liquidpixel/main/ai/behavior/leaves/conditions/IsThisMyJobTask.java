package com.liquidpixel.main.ai.behavior.leaves.conditions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.liquidpixel.main.ai.blackboards.BehaviorBlackboard;
import com.liquidpixel.main.components.agent.AgentJobComponent;
import com.liquidpixel.main.model.ai.Job;

public class IsThisMyJobTask extends LeafTask<BehaviorBlackboard> {

    @TaskAttribute(required = true) public String job;

    public IsThisMyJobTask(String job) {
        super();
        this.job = job;
    }

    public IsThisMyJobTask() {
    }

    @Override
    protected Task copyTo(Task task) {
        return task;
    }

    @Override
    public Status execute() {

        BehaviorBlackboard behaviorBlackboard = getObject();
        Entity entity  = behaviorBlackboard.getEntity();

        AgentJobComponent agentJobComponent = entity.getComponent(AgentJobComponent.class);

        if(agentJobComponent == null) {
            return Status.FAILED;
        }

//        Job jobObject =  agentJobComponent.getJob();

//        if(jobObject.getId().equals(job)){
//            return Status.SUCCEEDED;
//        }

        return Status.FAILED;
    }

}
