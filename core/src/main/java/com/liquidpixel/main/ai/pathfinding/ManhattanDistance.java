package com.liquidpixel.main.ai.pathfinding;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.liquidpixel.main.ai.pathfinding.Node;

public class ManhattanDistance implements Heuristic<com.liquidpixel.main.ai.pathfinding.Node> {
    @Override
    public float estimate(com.liquidpixel.main.ai.pathfinding.Node node, Node endNode) {
        return Math.abs(endNode.getX() - node.getX()) + Math.abs(endNode.getY() - node.getY());
    }
}
