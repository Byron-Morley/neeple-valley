package com.liquidpixel.main.ai.pathfinding;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.pfa.Connection;
import com.liquidpixel.main.ai.pathfinding.FlatTiledNode;
import com.liquidpixel.main.ai.pathfinding.MapGraph;
import com.liquidpixel.main.utils.Mappers;

import java.util.List;

import static com.liquidpixel.main.utils.utils.BASE_COST;

public class FlatTiledConnection implements Connection<com.liquidpixel.main.ai.pathfinding.FlatTiledNode> {

    private static final float DIAGONAL_COST_MULTIPLIER = 1.414f;
    private com.liquidpixel.main.ai.pathfinding.FlatTiledNode fromNode;
    private com.liquidpixel.main.ai.pathfinding.FlatTiledNode toNode;
    private float cost;
    com.liquidpixel.main.ai.pathfinding.MapGraph mapGraph;

    public FlatTiledConnection(com.liquidpixel.main.ai.pathfinding.MapGraph mapGraph, com.liquidpixel.main.ai.pathfinding.FlatTiledNode fromNode, com.liquidpixel.main.ai.pathfinding.FlatTiledNode toNode) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.mapGraph = mapGraph;
        this.cost = BASE_COST;

        if (isDiagonalConnection(fromNode, toNode)) {
            this.cost *= DIAGONAL_COST_MULTIPLIER; // Apply diagonal cost
        }
    }


    public FlatTiledConnection(MapGraph mapGraph, com.liquidpixel.main.ai.pathfinding.FlatTiledNode fromNode, com.liquidpixel.main.ai.pathfinding.FlatTiledNode toNode, float cost) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.mapGraph = mapGraph;
        this.cost = cost;
    }

    @Override
    public float getCost() {
        return cost;
    }


    public void setCost(float cost) {
        this.cost = cost;
    }

    public void calculateCost() {
        float baseCost = BASE_COST;

        if (isDiagonalConnection(fromNode, toNode)) {
            baseCost *= DIAGONAL_COST_MULTIPLIER; // Apply diagonal cost
        }

        float costMultiplier = 1.0f;
        List<Entity> entities = toNode.getEntities();
        for (Entity entity : entities) {
            if (Mappers.path.has(entity)) {
                costMultiplier = 0.01f; // Your existing logic
            }
        }

        setCost(baseCost * costMultiplier);
    }


    private boolean isDiagonalConnection(com.liquidpixel.main.ai.pathfinding.FlatTiledNode from, com.liquidpixel.main.ai.pathfinding.FlatTiledNode to) {
        // For diagonal connections, both x and y coordinates differ by exactly 1
        int dx = Math.abs(from.getX() - to.getX());
        int dy = Math.abs(from.getY() - to.getY());

        // Diagonal means both x and y change by exactly 1
        return dx == 1 && dy == 1;
    }


    @Override
    public com.liquidpixel.main.ai.pathfinding.FlatTiledNode getFromNode() {
        return fromNode;
    }

    @Override
    public FlatTiledNode getToNode() {
        return toNode;
    }
}
