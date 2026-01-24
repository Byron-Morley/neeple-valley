package com.liquidpixel.main.ai.pathfinding;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Array;
import com.liquidpixel.main.ai.pathfinding.FlatTiledConnection;
import com.liquidpixel.main.ai.pathfinding.TiledNode;
import com.liquidpixel.main.model.sprite.NodeType;
import com.liquidpixel.core.core.Direction;
import com.liquidpixel.main.utils.Mappers;
import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;
import java.util.List;

/*
FlatTiledNode. TILE_EMPTY = 0;
FlatTiledNode. TILE_FLOOR = 1;
FlatTiledNode. TILE_WALL = 2;
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class FlatTiledNode extends TiledNode<FlatTiledNode> {

    @JsonProperty
    private int index;

    @JsonProperty
    private Direction direction;

    @JsonProperty
    private List<Entity> entities;

    private boolean hasPathModifier = false;

    @JsonCreator
    public FlatTiledNode(
        @JsonProperty("x") int x,
        @JsonProperty("y") int y,
        @JsonProperty("type") int type,
        @JsonProperty("connections") Array<Connection<FlatTiledNode>> connections,
        @JsonProperty("index") int index,
        @JsonProperty("direction") Direction direction,
        @JsonProperty("entities") List<Entity> entities
    ) {
        super(x, y, NodeType.fromValue(type), connections);
        this.index = index;
        this.direction = direction;
        this.entities = entities;
    }

    public FlatTiledNode(int x, int y, NodeType type, int connectionCapacity, int index) {
        super(x, y, type, new Array<>(connectionCapacity));
        this.index = index;
        entities = new ArrayList<>();
    }

    @Override
    public int getIndex() {
        return index;
    }

    @JsonIgnore
    public GridPoint2 getLocation() {
        return new GridPoint2(x, y);
    }

    @JsonIgnore
    public boolean isWalkable() {
        return (getType() == NodeType.TILE_FLOOR);
    }

    public void clearConnections() {
        setConnections(new Array<>(getConnections().size));
        if (hasPathModifier) {
            recalculateConnectionCosts();
            hasPathModifier = false;
        }
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        if (Mappers.path.has(entity)) {
            hasPathModifier = true;
            recalculateConnectionCosts();
        }
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
        if (hasPathModifier) {
            recalculateConnectionCosts();
        }
        if (Mappers.path.has(entity)) {
            hasPathModifier = false;
        }
    }

    private void recalculateConnectionCosts() {
        // Get all connections to this node and recalculate their costs
        Array<Connection<FlatTiledNode>> connections = getConnections();
        for (Connection<FlatTiledNode> connection : connections) {
            if (connection instanceof FlatTiledConnection) {
                ((FlatTiledConnection) connection).calculateCost();
            }
        }

        // Also update incoming connections (where this node is the "to" node)
        // You might need to traverse all connections in the graph for this
        // or maintain a list of incoming connections
        for (FlatTiledNode neighbor : getNeighbors()) {
            Array<Connection<FlatTiledNode>> neighborConnections = neighbor.getConnections();
            for (Connection<FlatTiledNode> connection : neighborConnections) {
                if (connection.getToNode() == this && connection instanceof FlatTiledConnection) {
                    ((FlatTiledConnection) connection).calculateCost();
                }
            }
        }
    }

    private List<FlatTiledNode> getNeighbors() {
        // Implement this to return all connected nodes
        // This might require accessing the MapGraph
        // Or you could maintain a list of neighbors
        // This is a placeholder implementation
        List<FlatTiledNode> neighbors = new ArrayList<>();
        Array<Connection<FlatTiledNode>> connections = getConnections();
        for (Connection<FlatTiledNode> connection : connections) {
            neighbors.add(connection.getToNode());
        }
        return neighbors;
    }
}
