package com.liquidpixel.main.ai.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;
import com.liquidpixel.main.model.sprite.NodeType;

public abstract class TiledNode<N extends TiledNode<N>> {

    /** The x coordinate of this tile */
    public final int x;

    /** The y coordinate of this tile */
    public final int y;

    private NodeType type;

    protected Array<Connection<N>> connections;

    public TiledNode (int x, int y, NodeType type, Array<Connection<N>> connections) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.connections = connections;
    }

    public abstract int getIndex ();

    public Array<Connection<N>> getConnections () {
        return this.connections;
    }

    public void addConnection(Connection connection) {
        connections.add(connection);
    }

    public void setConnections(Array<Connection<N>> connections) {
        this.connections = connections;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
