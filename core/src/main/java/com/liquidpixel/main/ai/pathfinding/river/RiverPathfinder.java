package com.liquidpixel.main.ai.pathfinding.river;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.ai.pathfinding.*;
import com.liquidpixel.main.engine.GameState;
import com.liquidpixel.main.generators.map.PathFinder;
import com.liquidpixel.main.interfaces.INoiseGenerator;
import com.liquidpixel.main.model.sprite.NodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RiverPathfinder {
    private INoiseGenerator noiseGenerator;
    private Random random;
    private int paddingSize = 32; // Default padding size in tiles
    private List<GridPoint2> riverPath = new ArrayList<>();

    public RiverPathfinder(INoiseGenerator noiseGenerator) {
        this.noiseGenerator = noiseGenerator;
        this.random = new Random(GameState.getSeed());
    }

    public void setPaddingSize(int paddingSize) {
        this.paddingSize = paddingSize;
    }

    /**
     * Generate a river path from one side to another
     *
     * @param width          The width of the visible map
     * @param height         The height of the visible map
     * @param startSide      The side to start from (0=top, 1=right, 2=bottom, 3=left)
     * @param endSide        The side to end at (0=top, 1=right, 2=bottom, 3=left)
     * @param costMultiplier How much to multiply the cost by (higher = more influence of noise)
     * @return List of points representing the river path
     */
    public List<GridPoint2> generateRiverPath(int width, int height, int startSide, int endSide, float costMultiplier) {
        // Clear any existing path
        riverPath.clear();

        // Calculate the extended dimensions with padding
        int extendedWidth = width + (paddingSize * 2);
        int extendedHeight = height + (paddingSize * 2);

        Gdx.app.log("RiverPathfinder", "Generating river with extended area: " +
                    extendedWidth + "x" + extendedHeight + " (padding: " + paddingSize + ")");

        // Create the graph from noise using the extended dimensions
        MapGraph graph = createMapGraphFromNoise(extendedWidth, extendedHeight, costMultiplier);

        // Create pathfinder with the graph
        PathFinder pathFinder = new PathFinder(graph);

        // Generate start and end points on the specified sides, but in the extended space
        GridPoint2 start = getRandomPointOnSide(extendedWidth, extendedHeight, startSide);
        GridPoint2 end = getRandomPointOnSide(extendedWidth, extendedHeight, endSide);

        // Find the path using the pathfinder
        PathFinderResult result = pathFinder.searchNodePath(start, end);

        // Store the path if found
        if (result.isFound()) {
            // Get the path nodes
            TiledSmoothableGraphPath<FlatTiledNode> pathNodes = result.getPath();

            // Process the path and adjust coordinates back to the visible map space
            for (int i = 0; i < pathNodes.getCount(); i++) {
                FlatTiledNode node = pathNodes.get(i);

                // Adjust coordinates to account for padding
                int adjustedX = node.x - paddingSize;
                int adjustedY = node.y - paddingSize;

                // Only add points that are within the visible map
                if (adjustedX >= 0 && adjustedX < width && adjustedY >= 0 && adjustedY < height) {
                    riverPath.add(new GridPoint2(adjustedX, adjustedY));
                }
            }

            Gdx.app.log("RiverPathfinder", "River path generated with " + riverPath.size() +
                       " points (from extended path of " + pathNodes.getCount() + " points)");
        } else {
            Gdx.app.log("RiverPathfinder", "Failed to generate river path");
        }

        return riverPath;
    }

    /**
     * Creates a MapGraph based on the noise values
     */
    private MapGraph createMapGraphFromNoise(int width, int height, float costMultiplier) {
        MapGraph graph = new MapGraph(width, height);

        // Create all nodes first
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Create node (all nodes are walkable - TILE_FLOOR = 1)
                GridPoint2 position = new GridPoint2(x, y);
                int index = y * width + x;
                FlatTiledNode node = new FlatTiledNode(x, y, NodeType.TILE_FLOOR, 8, index);

                // Add node to graph
                graph.getNodes().put(position, node);
            }
        }

        // Now connect nodes with appropriate costs
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                GridPoint2 position = new GridPoint2(x, y);
                FlatTiledNode node = graph.getNode(position);

                // Connect to adjacent nodes (4-way)
                connectToNeighbor(graph, node, x + 1, y, width, height, costMultiplier);
                connectToNeighbor(graph, node, x - 1, y, width, height, costMultiplier);
                connectToNeighbor(graph, node, x, y + 1, width, height, costMultiplier);
                connectToNeighbor(graph, node, x, y - 1, width, height, costMultiplier);

                // Optionally connect diagonals
                if (graph.diagonal) {
                    connectToNeighbor(graph, node, x + 1, y + 1, width, height, costMultiplier);
                    connectToNeighbor(graph, node, x - 1, y + 1, width, height, costMultiplier);
                    connectToNeighbor(graph, node, x + 1, y - 1, width, height, costMultiplier);
                    connectToNeighbor(graph, node, x - 1, y - 1, width, height, costMultiplier);
                }
            }
        }

        return graph;
    }

    /**
     * Helper method to connect a node to its neighbor with a cost based on noise
     */
    private void connectToNeighbor(MapGraph graph, FlatTiledNode node, int nx, int ny, int width, int height, float costMultiplier) {
        // Check if neighbor is within bounds
        if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
            GridPoint2 neighborPos = new GridPoint2(nx, ny);
            FlatTiledNode neighbor = graph.getNode(neighborPos);
            if (neighbor != null) {
                // Get noise value at neighbor position
                float noiseValue = noiseGenerator.getValue(nx, ny, width, height);

                // Calculate cost - darker pixels (lower values) = higher cost
                // We invert the noise value and apply the multiplier
                float cost = (1.0f - noiseValue) * costMultiplier;

                // Ensure minimum cost of 1
                cost = Math.max(1.0f, cost);

                // Create connection with custom cost
                FlatTiledConnection connection = new FlatTiledConnection(graph, node, neighbor, cost);
                node.getConnections().add(connection);
            }
        }
    }

    /**
     * Get a random point on one of the sides of the extended map that has a high noise value
     */
    private GridPoint2 getRandomPointOnSide(int extendedWidth, int extendedHeight, int side) {
        // Minimum acceptable noise value (whiteness threshold)
        float minNoiseValue = 0.7f;

        // Maximum attempts to find a suitable point
        int maxAttempts = 100;

        // Calculate the visible map dimensions
        int visibleWidth = extendedWidth - (paddingSize * 2);
        int visibleHeight = extendedHeight - (paddingSize * 2);

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            GridPoint2 point;

            // Generate points on the extended map edges, but focus on the area
            // that corresponds to the visible map edges
            switch (side) {
                case 0: // Top
                    // Random X within the visible width, but shifted by padding
                    point = new GridPoint2(
                        paddingSize + random.nextInt(visibleWidth),
                        0  // Top edge of extended map
                    );
                    break;
                case 1: // Right
                    point = new GridPoint2(
                        extendedWidth - 1,  // Right edge of extended map
                        paddingSize + random.nextInt(visibleHeight)
                    );
                    break;
                case 2: // Bottom
                    point = new GridPoint2(
                        paddingSize + random.nextInt(visibleWidth),
                        extendedHeight - 1  // Bottom edge of extended map
                    );
                    break;
                case 3: // Left
                    point = new GridPoint2(
                        0,  // Left edge of extended map
                        paddingSize + random.nextInt(visibleHeight)
                    );
                    break;
                default:
                    point = new GridPoint2(0, 0);
                    break;
            }

            // Check if this point has a high enough noise value
            float noiseValue = noiseGenerator.getValue(point.x, point.y, extendedWidth, extendedHeight);
            if (noiseValue >= minNoiseValue) {
                return point;
            }
        }

        // If we couldn't find a suitable point after max attempts,
        // find the best point on that side
        GridPoint2 bestPoint = null;
        float bestNoiseValue = 0;

        // Sample points along the side to find the best one
        int sampleCount = Math.max(extendedWidth, extendedHeight);

        for (int i = 0; i < sampleCount; i++) {
            GridPoint2 point;

            switch (side) {
                case 0: // Top
                    // Sample across the top edge, but within the area corresponding to visible map
                    point = new GridPoint2(
                        paddingSize + (i % visibleWidth),
                        0
                    );
                    break;
                case 1: // Right
                    point = new GridPoint2(
                        extendedWidth - 1,
                        paddingSize + (i % visibleHeight)
                    );
                    break;
                case 2: // Bottom
                    point = new GridPoint2(
                        paddingSize + (i % visibleWidth),
                        extendedHeight - 1
                    );
                    break;
                case 3: // Left
                    point = new GridPoint2(
                        0,
                        paddingSize + (i % visibleHeight)
                    );
                    break;
                default:
                    point = new GridPoint2(0, 0);
                    break;
            }

            float noiseValue = noiseGenerator.getValue(point.x, point.y, extendedWidth, extendedHeight);
            if (noiseValue > bestNoiseValue) {
                bestNoiseValue = noiseValue;
                bestPoint = point;
            }
        }

        Gdx.app.log("RiverPathfinder", "Using best available point on side " + side +
            " with noise value " + bestNoiseValue);
        return bestPoint;
    }

    /**
     * Get the current river path
     */
    public List<GridPoint2> getRiverPath() {
        return riverPath;
    }

    /**
     * Get the padding size used for extended noise area
     */
    public int getPaddingSize() {
        return paddingSize;
    }
}
