package com.liquidpixel.pathfinding.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.GridPoint2;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class TraverseComponent implements Component {
    public enum State {MOVING, COMPLETE, FAILED}

    // In TraverseComponent class
    private int patience = 100; // Default patience value
    private int maxPatience = 100;

    @JsonProperty
    public GridPoint2 currentTarget;

    @JsonProperty
    public State state = State.MOVING;

    @JsonProperty
    private final GridPoint2 destination;

    @JsonProperty
    private final List<GridPoint2> waypoints = new ArrayList<>();

    @JsonCreator
    public TraverseComponent(
        @JsonProperty("currentTarget") GridPoint2 currentTarget,
        @JsonProperty("state") State state,
        @JsonProperty("destination") GridPoint2 destination,
        @JsonProperty("waypoints") List<GridPoint2> waypoints) {
        this.currentTarget = currentTarget;
        this.state = state;
        this.destination = destination;
        if (waypoints != null) {
            this.waypoints.addAll(waypoints);
        }
    }


    public boolean hasPatience() {
        return patience > 0;
    }

    public void decreasePatience() {
        patience = Math.max(0, patience - 1);
    }

    public void increasePatience() {
        patience = Math.min(maxPatience, patience + 1);
    }

    public TraverseComponent(GridPoint2 destination) {
        this.destination = destination;
    }

    public GridPoint2 getDestination() {
        return destination;
    }

    public List<GridPoint2> getWaypoints() {
        return waypoints;
    }

    public GridPoint2 getCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(GridPoint2 currentTarget) {
        this.currentTarget = currentTarget;
    }
}
