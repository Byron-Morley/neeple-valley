package com.liquidpixel.pathfinding.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.core.components.core.StatusComponent;
import com.liquidpixel.main.components.*;
import com.liquidpixel.pathfinding.components.TraverseComponent;
import com.liquidpixel.main.engine.GameState;
import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.core.core.Action;
import com.liquidpixel.core.core.Direction;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.main.utils.SteeringUtils;

import java.util.List;

public class TraverseSystem extends IteratingSystem {
    float epsilon = 0.1f;
    IMapService mapService;

    public TraverseSystem(IMapService mapService) {
        super(Family.all(TraverseComponent.class).get());
        this.mapService = mapService;
    }

    @Override
    protected void processEntity(Entity agent, float deltaTime) {
        TraverseComponent traverse = agent.getComponent(TraverseComponent.class);

        switch (traverse.state) {
            case MOVING:
                if (pathIsComplete(traverse, agent)) {
                    traverse.state = TraverseComponent.State.COMPLETE;
                    return;
                }

                if (traverse.getCurrentTarget() == null) {
                    List<GridPoint2> waypoints = traverse.getWaypoints();
                    if (!waypoints.isEmpty()) {
                        GridPoint2 newTarget = waypoints.remove(0);
                        traverse.setCurrentTarget(newTarget);
                        updateDirection(newTarget, agent);
                    }
                }

                handleMovement(agent, traverse);
                break;
            case COMPLETE:
            case FAILED:
                break;
        }
    }

    private void handleMovement(Entity agent, TraverseComponent traverse) {
        PositionComponent positionComponent = agent.getComponent(PositionComponent.class);
        Vector2 position = positionComponent.getPosition();
        GridPoint2 target = traverse.getCurrentTarget();

        if (target == null) return;

        Vector2 targetPosition = new Vector2(target.x, target.y);

        // Check if next tile is occupied before moving
        GridPoint2 currentGridPos = positionComponent.getGridPosition();
        GridPoint2 previousGridPos = positionComponent.getPreviousPosition();

        // Only check for blockage if we're actively moving between tiles
        if (!currentGridPos.equals(target)) {
            // Calculate movement direction
            Vector2 direction = new Vector2(targetPosition).sub(position).nor();

            // Determine the next tile we're going to enter
            GridPoint2 nextTile = determineNextTileFromDirection(currentGridPos, direction);

            // If the next tile is our target or we're already there, we don't need to check
            if (!nextTile.equals(currentGridPos) && !nextTile.equals(previousGridPos)) {
                // Check if next tile is occupied
                if (isOccupied(nextTile, agent)) {


                    // Enter waiting state
                    if (traverse.hasPatience()) {
                        traverse.decreasePatience();
//                        agent.getComponent(StatusComponent.class).setAction(Action.STANDING);
                        // We return without moving, effectively waiting
                        return;
                    } else {


//                        can we find an alternate route?


//                        agent.getComponent(StatusComponent.class).setAction(Action.WALKING);
                        // If out of patience, could implement alternative behaviors here
                        // For now, just try to continue
                    }
                } else {
                    // Path is clear, restore patience if needed
                    traverse.increasePatience();

                }
            }
        }

        if (readyForNextWaypoint(targetPosition, position)) {
            positionComponent.setPosition(target);
            traverse.setCurrentTarget(null);
        } else {
            updatePosition(targetPosition, position, agent);
        }
    }

    private GridPoint2 determineNextTileFromDirection(GridPoint2 currentPos, Vector2 direction) {
        int dx = 0;
        int dy = 0;

        // Determine primary movement direction
        if (Math.abs(direction.x) > Math.abs(direction.y)) {
            // Moving primarily horizontally
            dx = direction.x > 0 ? 1 : -1;
        } else {
            // Moving primarily vertically
            dy = direction.y > 0 ? 1 : -1;
        }

        return new GridPoint2(currentPos.x + dx, currentPos.y + dy);
    }

    private boolean isOccupied(GridPoint2 position, Entity agent) {

        List<Entity> entities = mapService.getWorldMap().getEntitiesAtPosition(position);

        return mapService.getWorldMap().getEntitiesAtPosition(position).stream()
            .filter(entity -> !Mappers.camera.has(entity))
            .filter(entity -> !agent.equals(entity))
            .filter(Mappers.agent::has)
            .anyMatch(Mappers.worker::has);
    }

    private void updatePosition(Vector2 target, Vector2 position, Entity agent) {
        float speed = Mappers.velocity.get(agent).getVelocity();
        Vector2 direction = new Vector2(target).sub(position).nor();
        float distance = position.dst(target);
        float moveAmount = Math.min(speed * Gdx.graphics.getDeltaTime() * GameState.getTimeScale(), distance);
        agent.getComponent(PositionComponent.class).setPosition(position.add(direction.scl(moveAmount)));
    }

    private boolean readyForNextWaypoint(Vector2 target, Vector2 position) {
        return position.epsilonEquals(target, epsilon);
    }

    private boolean pathIsComplete(TraverseComponent traverse, Entity agent) {
        GridPoint2 agentPosition = agent.getComponent(PositionComponent.class).getGridPosition();
        return (traverse.getWaypoints().isEmpty() &&
            traverse.getCurrentTarget() == null &&
            agentPosition.equals(traverse.getDestination()));
    }

    private void updateDirection(GridPoint2 target, Entity agent) {
        Direction direction = SteeringUtils.getDirection(
            agent.getComponent(PositionComponent.class).getPosition(),
            new Vector2(target.x, target.y)
        );

        agent.getComponent(StatusComponent.class).setDirection(direction);
        agent.getComponent(StatusComponent.class).setAction(Action.WALKING);
    }
}
