package com.liquidpixel.main.ai.pathfinding;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.liquidpixel.main.ai.pathfinding.TiledNode;

public class TiledManhattanDistance<N extends TiledNode<N>> implements Heuristic<N> {

    public TiledManhattanDistance() {
    }

    @Override
    public float estimate(N node, N endNode) {
        return 0; // This makes A* behave like Dijkstra's algorithm
    }

}
