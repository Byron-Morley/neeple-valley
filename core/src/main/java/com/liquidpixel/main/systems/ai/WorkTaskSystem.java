package com.liquidpixel.main.systems.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.liquidpixel.main.components.ai.actions.DoWorkComponent;
import com.liquidpixel.main.components.ai.actions.WorkTaskComponent;
import com.liquidpixel.main.interfaces.IRecipe;
import com.liquidpixel.main.utils.Mappers;

public class WorkTaskSystem extends IteratingSystem {

    public WorkTaskSystem() {
        super(Family.all(WorkTaskComponent.class).get());
    }

    @Override
    protected void processEntity(Entity agent, float deltaTime) {

        WorkTaskComponent workTask = agent.getComponent(WorkTaskComponent.class);

        switch (workTask.state) {
            case WORKING:
                handleWorkingState(agent, workTask, deltaTime);
                break;
            case COMPLETED:
                handleCompletedState(agent, workTask);
                break;
            case FAILED:
                break;
        }
    }

    private void handleWorkingState(Entity agent, WorkTaskComponent workTask, float deltaTime) {
        IRecipe recipe = workTask.getRecipe();
        if (recipeIsComplete(recipe, workTask)) {
            workTask.state = WorkTaskComponent.State.COMPLETED;
        } else {
            workTask.setProgress(workTask.getProgress() + deltaTime);
        }
    }

    private void handleCompletedState(Entity agent, WorkTaskComponent workTask) {
        if(Mappers.dowork.has(agent)) {
            agent.getComponent(DoWorkComponent.class).state = DoWorkComponent.State.COMPLETED;
        }
//        agent.remove(WorkTaskComponent.class);
    }

    private boolean recipeIsComplete(IRecipe recipe, WorkTaskComponent workTask) {
        return workTask.getProgress() >= recipe.getEffort();
    }
}
