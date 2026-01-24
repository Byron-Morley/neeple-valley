package com.liquidpixel.main.ai.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

public class ChunkNode {
    protected Array<Connection<ChunkNode>> connections;

    public ChunkNode() {
        connections = new Array<Connection<ChunkNode>>();
    }

    public Array<Connection<ChunkNode>> getConnections() {
        return connections;
    }
}
