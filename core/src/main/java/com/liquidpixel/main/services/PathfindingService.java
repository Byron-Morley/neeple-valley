package com.liquidpixel.main.services;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.ai.pathfinding.*;
import com.liquidpixel.main.interfaces.IPathfinding;

public class PathfindingService extends Service implements IPathfinding {

    TiledSmoothableGraphPath<FlatTiledNode> path;
    TiledManhattanDistance<FlatTiledNode> heuristic;
    IndexedAStarPathFinder<FlatTiledNode> pathFinder;
    //    PathSmoother<FlatTiledNode, Vector2> pathSmoother;
    private CostAwarePathSmoother pathSmoother;

    MapGraph mapGraph;
    boolean pathSmoothing = false;
    private float pathDensityThreshold = 0.3f;


    public PathfindingService(MapGraph mapGraph) {
        this.mapGraph = mapGraph;
        heuristic = new TiledManhattanDistance<>();
        pathFinder = new IndexedAStarPathFinder<>(mapGraph, true);
//        pathSmoother = new PathSmoother<>(new TiledRaycastCollisionDetector<>(mapGraph));
//        CostAwareRaycastCollisionDetector<FlatTiledNode> costAwareDetector = ;
        // Create the path smoother with the cost-aware detector
//        pathSmoother = new PathSmoother<>(new CostAwareRaycastCollisionDetector<>(mapGraph, pathDensityThreshold));

        pathSmoother = new CostAwarePathSmoother(mapGraph, 40.0f);

    }

    public PathFinderResult searchNodePath(GridPoint2 from, GridPoint2 to) {
        // Get the corresponding nodes from the grid points
        FlatTiledNode fromNode = mapGraph.getNode(from);
        FlatTiledNode toNode = mapGraph.getNode(to);

        // Create a path object to store the result
        TiledSmoothableGraphPath<FlatTiledNode> path = new TiledSmoothableGraphPath<>();

        // Check if nodes are valid
        if (fromNode == null || toNode == null) {
            return new PathFinderResult(null, false);
        }

        // Search for a path
        boolean found = pathFinder.searchNodePath(fromNode, toNode, heuristic, path);

        // If a path was found and smoothing is enabled, smooth it
        if (found && pathSmoothing) {
            pathSmoother.smoothPath(path);
        }

        // Return the result
        return new PathFinderResult(path, found);
    }

    public boolean hasValidPath(GridPoint2 from, GridPoint2 to) {
        path = new TiledSmoothableGraphPath<>();
        FlatTiledNode fromNode;
        FlatTiledNode toNode;

        try {
            fromNode = mapGraph.getNode(from);
            toNode = mapGraph.getNode(to);

            if (fromNode == null || toNode == null) {
                return false;
            }
        } catch (NullPointerException e) {
            return false;
        }

        return pathFinder.searchNodePath(fromNode, toNode, heuristic, path);

    }


    private void removeStandingCell(TiledSmoothableGraphPath<FlatTiledNode> path) {
        path.pop();
    }
}
