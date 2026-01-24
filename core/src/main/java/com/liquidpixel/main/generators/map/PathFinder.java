package com.liquidpixel.main.generators.map;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.ai.pathfinding.*;

import java.util.Random;

public class PathFinder {
    private TiledSmoothableGraphPath<FlatTiledNode> path;
    private TiledManhattanDistance<FlatTiledNode> heuristic;
    private IndexedAStarPathFinder<FlatTiledNode> pathFinder;
    private PathSmoother<FlatTiledNode, Vector2> pathSmoother;
    private MapGraph mapGraph;
    private boolean pathSmoothing = false;

    private float heuristicWeight = 0.5f;
WeightedHeuristic weightedHeuristic;

    public PathFinder(MapGraph mapGraph, boolean pathSmoothing) {
        this.pathSmoothing = pathSmoothing;
        init(mapGraph);
    }

    public PathFinder(MapGraph mapGraph) {
        init(mapGraph);
    }

    private void init(MapGraph mapGraph) {
        this.mapGraph = mapGraph;
        heuristic = new TiledManhattanDistance<FlatTiledNode>();
        weightedHeuristic = new WeightedHeuristic(heuristic, heuristicWeight);

        pathFinder = new IndexedAStarPathFinder<FlatTiledNode>(mapGraph, true);
        pathSmoother = new PathSmoother<FlatTiledNode, Vector2>(new TiledRaycastCollisionDetector<FlatTiledNode>(mapGraph));
    }

    public PathFinderResult searchNodePath(GridPoint2 from, GridPoint2 to) {
//        System.out.println("fromNode or toNode is: " + from + " or " + to);
        path = new TiledSmoothableGraphPath<FlatTiledNode>();
        FlatTiledNode fromNode = null;
        FlatTiledNode toNode = null;

        try {
            fromNode = mapGraph.getNode(from);
            toNode = mapGraph.getNode(to);

            if (fromNode == null || toNode == null) {
                return new PathFinderResult(path, false);
            }
        } catch (NullPointerException e) {
            return new PathFinderResult(path, false);
        }

        boolean pathFound = pathFinder.searchNodePath(fromNode, toNode, weightedHeuristic
, path);

        if (pathSmoothing) pathSmoother.smoothPath(path);

        if (pathFound) {
            removeStandingCell(path);
        }

        return new PathFinderResult(path, pathFound);
    }

    private void removeStandingCell(TiledSmoothableGraphPath<FlatTiledNode> path) {
        path.pop();
    }

//    public Vector2 getRandomWalkablePosition() {
//        float dx = 0;
//        float dy = 0;
//
//        Random r = new Random();
//        boolean found = false;
//
//        while (!found) {
//
//            int x = r.nextInt(mapGraph.getWidth());
//            int y = r.nextInt(mapGraph.getHeight());
//            FlatTiledNode node = mapGraph.getNode(x, y);
//
//            if (node.type == 1) {
//                dx = node.x;
//                dy = node.y;
//
//                found = true;
//            }
//        }
//
//        return new Vector2(dx, dy);
//    }

    public boolean isPathSmoothing() {
        return pathSmoothing;
    }

    public void setPathSmoothing(boolean pathSmoothing) {
        this.pathSmoothing = pathSmoothing;
    }

    public boolean isReady() {
        return mapGraph.isReady();
    }

    // Then create a custom heuristic wrapper
    public class WeightedHeuristic implements Heuristic<FlatTiledNode> {
        private TiledManhattanDistance<FlatTiledNode> baseHeuristic;
        private float weight;

        public WeightedHeuristic(TiledManhattanDistance<FlatTiledNode> baseHeuristic, float weight) {
            this.baseHeuristic = baseHeuristic;
            this.weight = weight;
        }

        @Override
        public float estimate(FlatTiledNode node, FlatTiledNode endNode) {
            return baseHeuristic.estimate(node, endNode) * weight;
        }
    }

}
