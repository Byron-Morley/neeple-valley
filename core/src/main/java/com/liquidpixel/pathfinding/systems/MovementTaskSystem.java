package com.liquidpixel.pathfinding.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.ai.behavior.leaves.actions.poolable.FindPathTask;
import com.liquidpixel.main.ai.tasks.TaskManager;
import com.liquidpixel.pathfinding.components.MovementTaskComponent;
import com.liquidpixel.pathfinding.components.TraverseComponent;
import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.core.core.Action;
import com.liquidpixel.main.utils.Mappers;

import java.util.List;

public class MovementTaskSystem extends IntervalIteratingSystem {
    static final float INTERVAL_IN_SECONDS = 0.1f;
    TaskManager taskManager;
    IMapService mapService;

    public MovementTaskSystem(IMapService mapService, TaskManager taskManager) {
        super(Family.all(MovementTaskComponent.class, PositionComponent.class).get(), INTERVAL_IN_SECONDS);
        this.taskManager = taskManager;
        this.mapService = mapService;
    }

    @Override
    protected void processEntity(Entity agent) {
//        if (GameState.isPaused()) return;
        MovementTaskComponent movement = agent.getComponent(MovementTaskComponent.class);
        if (movement == null) return;
        switch (movement.state) {
            case IDLE:
                handleIdleState(agent, movement);
                break;
            case SEARCHING_FOR_PATH:
                handleSearchState(agent, movement);
                break;
            case PATH_FOUND:
                handlePathFoundState(agent, movement);
                break;
            case MOVING:
                handleMoveState(agent, movement);
                break;
            case FINISHED, FAILED:
                handleFinishedState(agent);
                break;
        }
    }

    private void handleIdleState(Entity agent, MovementTaskComponent movement) {
        GridPoint2 agentPosition = agent.getComponent(PositionComponent.class).getGridPosition();
        GridPoint2 targetPosition = movement.getTarget();

        FindPathTask findPathTask = new FindPathTask(
            mapService,
            agentPosition,
            targetPosition,
            movement
        );

        movement.state = MovementTaskComponent.State.SEARCHING_FOR_PATH;
        taskManager.getTaskRunner().addToQueue(findPathTask);
    }

    private void handleSearchState(Entity agent, MovementTaskComponent movement) {
        // Wait for FindPathTask to update state
    }

    private void handlePathFoundState(Entity agent, MovementTaskComponent movement) {
        List<GridPoint2> waypoints = movement.getWaypoints();
        if (waypoints.isEmpty()) {
            movement.state = MovementTaskComponent.State.FINISHED;
            return;
        }

        TraverseComponent traverse = new TraverseComponent(waypoints.get(waypoints.size() - 1));
        traverse.getWaypoints().addAll(waypoints);
        agent.add(traverse);
        movement.state = MovementTaskComponent.State.MOVING;
    }

    private void handleMoveState(Entity agent, MovementTaskComponent movement) {
        TraverseComponent traverse = agent.getComponent(TraverseComponent.class);
        if (traverse.state == TraverseComponent.State.COMPLETE ||
            traverse.state == TraverseComponent.State.FAILED) {
            movement.state = MovementTaskComponent.State.FINISHED;
            agent.remove(TraverseComponent.class);
        }
    }

    private void handleFinishedState(Entity agent) {
        Mappers.status.get(agent).setAction(Action.STANDING);
        Mappers.movetask.get(agent).dispose();
        agent.remove(MovementTaskComponent.class);
    }
}
