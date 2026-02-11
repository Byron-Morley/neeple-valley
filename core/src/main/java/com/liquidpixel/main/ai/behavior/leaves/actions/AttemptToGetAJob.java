package com.liquidpixel.main.ai.behavior.leaves.actions;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.liquidpixel.main.ai.blackboards.BehaviorBlackboard;
import com.liquidpixel.main.components.agent.AgentJobComponent;
import com.liquidpixel.main.components.workshop.JobComponent;
import com.liquidpixel.main.model.ai.Job;

import java.util.List;

import static com.liquidpixel.main.utils.utils.getList;

public class AttemptToGetAJob extends LeafTask<BehaviorBlackboard> {
    @Override
    public Status execute() {

        BehaviorBlackboard behaviorBlackboard = getObject();
        Entity entity = behaviorBlackboard.getEntity();
        Engine engine = behaviorBlackboard.getEngine();

        AgentJobComponent agentJobComponent = entity.getComponent(AgentJobComponent.class);
        if (agentJobComponent != null) {
            return Status.FAILED;
        }

        List<Entity> workshops = getList(engine.getEntitiesFor(Family.all(JobComponent.class).get()));

        for (Entity workshop : workshops) {
            JobComponent jobComponent = workshop.getComponent(JobComponent.class);

            if (jobComponent.hasJobAvailable()) {

//                String jobString = jobComponent.getJob();
//                Job job = new Job(jobString, workshop, entity);
//                jobComponent.assignJob(job);
//                entity.add(new AgentJobComponent(job));

                return Status.SUCCEEDED;
            }
        }

        return Status.FAILED;
    }

    @Override
    protected Task<BehaviorBlackboard> copyTo(Task<BehaviorBlackboard> task) {
        return task;
    }
}
