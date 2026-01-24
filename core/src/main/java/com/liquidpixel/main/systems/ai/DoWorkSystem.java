package com.liquidpixel.main.systems.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.liquidpixel.pathfinding.components.MovementTaskComponent;
import com.liquidpixel.main.components.ai.actions.DoWorkComponent;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.main.components.storage.StorageComponent;

public class DoWorkSystem extends IteratingSystem {

    IMapService mapService;
    IItemService itemService;

    public DoWorkSystem(IMapService mapService, IItemService itemService) {
        super(Family.all(
            DoWorkComponent.class
        ).get());
        this.mapService = mapService;
        this.itemService = itemService;
    }

    @Override
    protected void processEntity(Entity agent, float delta) {
        DoWorkComponent work = agent.getComponent(DoWorkComponent.class);

        switch (work.state) {
            case IDLE:
                handleIdleState(agent, work);
                break;
            case MOVING_TO_WORKSHOP:
                handleMovingState(agent, work);
                break;
            case SELECT_RECIPE:
                handleRecipeSelection(agent, work);
                break;
            case WORKING:
                handleWorkingState(agent, work);
                break;
            case COMPLETED:
                break;
        }
    }

    private void handleWorkingState(Entity agent, DoWorkComponent work) {
//        System.out.println("hardly Working");
    }

    private void handleMovingState(Entity agent, DoWorkComponent work) {
        MovementTaskComponent movement = agent.getComponent(MovementTaskComponent.class);
        if (movement == null || movement.state == MovementTaskComponent.State.FINISHED) {
            work.state = DoWorkComponent.State.SELECT_RECIPE;
        }
    }

    private void handleIdleState(Entity agent, DoWorkComponent work) {
//        GridPoint2 agentPosition = agent.getComponent(PositionComponent.class).getGridPosition();
//        Entity workshop = work.getWorkshop();
//        GridPoint2 workshopPosition = storageService.getStoragePosition(workshop);
//        GridPoint2 target = mapService.findValidInteractionPoint(agentPosition, workshopPosition);
//
//        if (target == null) {
//            work.state = DoWorkComponent.State.FAILED;
//            return;
//        }
//
//        if (target.equals(agentPosition)) {
//            work.state = DoWorkComponent.State.SELECT_RECIPE;
//        } else {
//            agent.add(new MovementTaskComponent(target));
//            work.state = DoWorkComponent.State.MOVING_TO_WORKSHOP;
//        }
    }

    private void handleRecipeSelection(Entity agent, DoWorkComponent work) {
        Entity workshop = work.getWorkshop();
        StorageComponent workshopStorageComponent = workshop.getComponent(StorageComponent.class);
//        IRecipe recipe = storageService.getWorkableRecipe(workshopStorageComponent);
//
//        if (recipe == null) {
//            work.state = DoWorkComponent.State.FAILED;
//            return;
//        }
//
//        agent.add(new WorkTaskComponent(recipe));
//        work.state = DoWorkComponent.State.WORKING;
    }
}
