package com.liquidpixel.main.ai.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

public class CostAwarePathSmoother {
    private final MapGraph worldMap;
    private final TiledRaycastCollisionDetector<FlatTiledNode> collisionDetector;
    private final float maxDeviationCost;

    // Parameters for Curvature Corrected Moving Average
    private final float curvatureWeight = 0.7f;  // Weight for curvature correction
    private final int windowSize = 3;            // Size of the moving average window

    public CostAwarePathSmoother(MapGraph worldMap, float maxDeviationCost) {
        this.worldMap = worldMap;
        this.collisionDetector = new TiledRaycastCollisionDetector<>(worldMap);
        this.maxDeviationCost = maxDeviationCost;
    }

    /**
     * Smooths the given path using Curvature Corrected Moving Average technique.
     * This approach reduces sharp turns while still respecting collision constraints
     * and terrain costs.
     *
     * @param path The path to smooth
     */
    public void smoothPath(DefaultGraphPath<FlatTiledNode> path) {
        if (path.getCount() <= 2) {
            return; // No smoothing needed for paths with 0-2 nodes
        }

        // Create a working copy of the path
        List<FlatTiledNode> originalPath = new ArrayList<>();
        for (int i = 0; i < path.getCount(); i++) {
            originalPath.add(path.get(i));
        }

        // Apply cost-aware path smoothing first to remove unnecessary nodes
        applyCostAwareSmoothing(path, originalPath);

        // Apply curvature corrected moving average for remaining path
        applyCurvatureCorrectedMovingAverage(path);
    }

    /**
     * Applies traditional cost-aware path smoothing to remove unnecessary nodes.
     */
    private void applyCostAwareSmoothing(DefaultGraphPath<FlatTiledNode> path, List<FlatTiledNode> originalPath) {
        // Clear the original path
        path.clear();

        // Start with the first node
        path.add(originalPath.get(0));

        int currentIndex = 0;
        while (currentIndex < originalPath.size() - 1) {
            int furthestVisible = currentIndex + 1;

            // Find the furthest node that has line of sight from the current node
            // and where the direct path doesn't exceed our cost threshold
            for (int i = currentIndex + 2; i < originalPath.size(); i++) {
                FlatTiledNode start = originalPath.get(currentIndex);
                FlatTiledNode target = originalPath.get(i);

                if (hasLineOfSight(start, target)) {
                    float directCost = getDirectCost(start, target);
                    float originalCost = getOriginalPathCost(originalPath, currentIndex, i);

                    if (directCost <= originalCost * (1 + maxDeviationCost)) {
                        furthestVisible = i;
                    }
                } else {
                    break; // Stop at first collision
                }
            }

            // Add the furthest visible node to the path
            path.add(originalPath.get(furthestVisible));
            currentIndex = furthestVisible;
        }
    }

    /**
     * Applies Curvature Corrected Moving Average to smooth sharp turns in the path.
     * This technique adjusts path nodes based on their neighbors while accounting for
     * curvature (changes in direction).
     */
    private void applyCurvatureCorrectedMovingAverage(DefaultGraphPath<FlatTiledNode> path) {
        if (path.getCount() <= 2) {
            return; // No need for this smoothing on short paths
        }

        // Create a working copy
        List<FlatTiledNode> originalNodes = new ArrayList<>();
        for (int i = 0; i < path.getCount(); i++) {
            originalNodes.add(path.get(i));
        }

        // Don't modify first and last nodes
        List<FlatTiledNode> smoothedNodes = new ArrayList<>();
        smoothedNodes.add(originalNodes.get(0));

        // Apply smoothing to middle nodes
        for (int i = 1; i < originalNodes.size() - 1; i++) {
            FlatTiledNode current = originalNodes.get(i);

            // Calculate window bounds for the moving average
            int startIdx = Math.max(0, i - windowSize/2);
            int endIdx = Math.min(originalNodes.size() - 1, i + windowSize/2);

            // Calculate average position
            float avgX = 0, avgY = 0;
            float totalWeight = 0;

            for (int j = startIdx; j <= endIdx; j++) {
                FlatTiledNode node = originalNodes.get(j);

                // Calculate curvature weight based on distance from the center node
                float distance = 1.0f - (Math.abs(j - i) / (float)(windowSize/2 + 1));
                float weight = distance;

                // Use x and y coordinates directly from the node
                avgX += node.x * weight;
                avgY += node.y * weight;
                totalWeight += weight;
            }

            avgX /= totalWeight;
            avgY /= totalWeight;

            // Calculate proposed new position
            int newX = Math.round(avgX);
            int newY = Math.round(avgY);

            // Create potential new node
            FlatTiledNode potentialNode = worldMap.getNode(new GridPoint2(newX, newY));

            // Check if we can safely move to this node
            if (potentialNode != null &&
                hasLineOfSight(originalNodes.get(i-1), potentialNode) &&
                hasLineOfSight(potentialNode, originalNodes.get(i+1))) {

                // Apply curvature correction
                Vector2 prev = new Vector2(originalNodes.get(i-1).x, originalNodes.get(i-1).y);
                Vector2 curr = new Vector2(current.x, current.y);
                Vector2 next = new Vector2(originalNodes.get(i+1).x, originalNodes.get(i+1).y);

                // Calculate vectors for direction changes
                Vector2 v1 = new Vector2(curr).sub(prev).nor();
                Vector2 v2 = new Vector2(next).sub(curr).nor();

                // Calculate the dot product to measure direction change
                float dot = v1.dot(v2);
                // Higher curvature (lower dot product) means we preserve more of the original path
                float curvatureFactor = (1 - dot) * curvatureWeight;

                // Blend between original and smoothed position based on curvature
                Vector2 correctedPos = new Vector2(
                    curr.x * curvatureFactor + newX * (1 - curvatureFactor),
                    curr.y * curvatureFactor + newY * (1 - curvatureFactor)
                );

                int correctedX = Math.round(correctedPos.x);
                int correctedY = Math.round(correctedPos.y);

                FlatTiledNode correctedNode = worldMap.getNode(new GridPoint2(correctedX, correctedY));
                if (correctedNode != null) {
                    smoothedNodes.add(correctedNode);
                } else {
                    // Fall back to original if corrected node is invalid
                    smoothedNodes.add(current);
                }
            } else {
                // Fall back to original node if we can't establish line of sight
                smoothedNodes.add(current);
            }
        }

        // Add the last node
        smoothedNodes.add(originalNodes.get(originalNodes.size() - 1));

        // Replace the path with smoothed nodes
        path.clear();
        for (FlatTiledNode node : smoothedNodes) {
            path.add(node);
        }
    }

    private boolean hasLineOfSight(FlatTiledNode start, FlatTiledNode target) {
        Ray<Vector2> ray = new Ray<>(new Vector2(start.x, start.y), new Vector2(target.x, target.y));
        return !collisionDetector.collides(ray);
    }

    private float getDirectCost(FlatTiledNode start, FlatTiledNode target) {
        // Calculate direct cost between nodes considering terrain costs from connections
        float distance = Vector2.dst(start.x, start.y, target.x, target.y);

        // Look for direct connections to estimate terrain cost factor
        float costMultiplier = 1.0f;
        boolean connectionFound = false;

        // Try to find a direct connection to get an accurate cost multiplier
        for (Connection<FlatTiledNode> connection : start.getConnections()) {
            FlatTiledNode toNode = connection.getToNode();
            // If there's a direct connection, use its cost per unit distance
            if (toNode == target) {
                float connDistance = Vector2.dst(start.x, start.y, toNode.x, toNode.y);
                if (connDistance > 0) {
                    costMultiplier = connection.getCost() / connDistance;
                    connectionFound = true;
                    break;
                }
            }
        }

        // If no direct connection found, estimate based on average of neighboring connections
        if (!connectionFound && !start.getConnections().isEmpty()) {
            float totalCostRatio = 0;
            int connCount = 0;

            for (Connection<FlatTiledNode> connection : start.getConnections()) {
                FlatTiledNode toNode = connection.getToNode();
                float connDistance = Vector2.dst(start.x, start.y, toNode.x, toNode.y);
                if (connDistance > 0) {
                    totalCostRatio += connection.getCost() / connDistance;
                    connCount++;
                }
            }

            if (connCount > 0) {
                costMultiplier = totalCostRatio / connCount;
            }
        }

        return distance * costMultiplier;
    }

    private float getOriginalPathCost(List<FlatTiledNode> path, int startIndex, int endIndex) {
        float cost = 0;
        for (int i = startIndex; i < endIndex; i++) {
            FlatTiledNode from = path.get(i);
            FlatTiledNode to = path.get(i + 1);

            // Get the connection cost if nodes are connected
            boolean connectionFound = false;
            for (Connection<FlatTiledNode> connection : from.getConnections()) {
                if (connection.getToNode() == to) {
                    cost += connection.getCost();
                    connectionFound = true;
                    break;
                }
            }

            // If no direct connection, estimate based on distance
            if (!connectionFound) {
                cost += Vector2.dst(from.x, from.y, to.x, to.y);
            }
        }
        return cost;
    }
}
