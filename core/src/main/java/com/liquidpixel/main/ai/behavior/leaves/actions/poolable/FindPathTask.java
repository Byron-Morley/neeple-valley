package com.liquidpixel.main.ai.behavior.leaves.actions.poolable;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.ai.blackboards.TaskBlackboard;
import com.liquidpixel.main.ai.pathfinding.PathFinderResult;
import com.liquidpixel.main.ai.tasks.PoolableTask;
import com.liquidpixel.pathfinding.components.MovementTaskComponent;
import com.liquidpixel.pathfinding.api.IMapService;
import java.util.ArrayList;
import java.util.List;

public class FindPathTask extends PoolableTask<TaskBlackboard> {
    IMapService mapService;
    GridPoint2 from;
    GridPoint2 to;
    MovementTaskComponent movement;

    public FindPathTask(IMapService mapService, GridPoint2 from, GridPoint2 to, MovementTaskComponent movement) {
        this.mapService = mapService;
        this.from = from;
        this.to = to;
        this.movement = movement;
    }

    @Override
    public void tick(float delta) {
        PathFinderResult pathFinderResult = mapService.getPathfindingService().searchNodePath(from, to);
        if (pathFinderResult.isFound()) {
            List<GridPoint2> waypoints = new ArrayList<>();
            for (int i = 0; i < pathFinderResult.getPath().getCount(); i++) {
                waypoints.add(pathFinderResult.getPath().get(i).getLocation());
            }
            movement.setWaypoints(waypoints);
            movement.state = MovementTaskComponent.State.PATH_FOUND;
            success();
        } else {
            movement.state = MovementTaskComponent.State.FAILED;
            fail();
        }
    }

    @Override
    protected Task copyTo(Task task) {
        return task;
    }
}
