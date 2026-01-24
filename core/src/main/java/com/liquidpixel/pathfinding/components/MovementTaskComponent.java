package com.liquidpixel.pathfinding.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.engine.GameResources;

import java.util.ArrayList;
import java.util.List;

public class MovementTaskComponent implements Component {
    public enum State {IDLE, SEARCHING_FOR_PATH, MOVING, PATH_FOUND, FINISHED, FAILED}

    Entity marker;

    public State state = State.IDLE;

    GridPoint2 target;

    List<GridPoint2> waypoints = new ArrayList<>();

    public MovementTaskComponent(
        State state,
        GridPoint2 target,
        List<GridPoint2> waypoints) {
        this.state = state;
        this.target = target;
        this.waypoints = waypoints;
    }

    public MovementTaskComponent(GridPoint2 target) {
        this.target = target;
    }

    public MovementTaskComponent(GridPoint2 target, Entity marker) {
        this.target = target;
        this.marker = marker;
    }

    public GridPoint2 getTarget() {
        return target;
    }

    public List<GridPoint2> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<GridPoint2> waypoints) {
        this.waypoints = waypoints;
    }

    public void dispose(){
        if(marker == null) return;
        GameResources.get().getEngine().removeEntity(marker);
    }

}
