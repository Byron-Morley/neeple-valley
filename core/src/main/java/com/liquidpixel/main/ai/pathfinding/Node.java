package com.liquidpixel.main.ai.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.utils.Array;

public class Node {
    private final int index;
    private final int x;
    private final int y;
    private final Array<Connection<Node>> connections;

    public Node(final int index, final int x, final int y, final int capacity) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.connections = new Array(capacity);
    }

    public int getIndex() {
        return index;
    }

    public Array<Connection<Node>> getConnections() {
        return connections;
    }

    public boolean hasConnections(){
        return connections.size > 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public void addConnection(DefaultConnection defaultConnection) {
        connections.add(defaultConnection);
    }
}
