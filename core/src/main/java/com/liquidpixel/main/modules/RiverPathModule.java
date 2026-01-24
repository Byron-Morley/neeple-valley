package com.liquidpixel.main.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.liquidpixel.main.engine.GameState;
import com.sudoplay.joise.module.*;
import com.sudoplay.joise.module.Module;

import java.util.*;

public class RiverPathModule {
    int seed;
    final Random random;
    Module module;
    private double scale = 0.1; // Base scale factor that worked well
    private int referenceSize = 1024; // Reference size for consistent pattern scaling

    // River path data
    private Set<Point> riverPath = new HashSet<>();

    public RiverPathModule() {
        random = new Random(GameState.getSeed());
        module = createRidgeMultiNoiseModule();
    }

    private Module createRidgeMultiNoiseModule() {
        // Create a Simplex RIDGEMULTI noise module
        ModuleFractal gen = new ModuleFractal();
        gen.setAllSourceBasisTypes(ModuleBasisFunction.BasisType.SIMPLEX);
        gen.setAllSourceInterpolationTypes(ModuleBasisFunction.InterpolationType.QUINTIC);
        gen.setNumOctaves(1);
        gen.setFrequency(0.03);
        gen.setType(ModuleFractal.FractalType.RIDGEMULTI);
        gen.setSeed(GameState.getSeed());

        // Auto-correct to ensure values are in the 0-1 range
        ModuleAutoCorrect source = new ModuleAutoCorrect(0, 1);
        source.setSource(gen);
        source.setSamples(10000);
        source.calculate2D();

        return source;
    }

    public float getValue(int x, int y, int width, int height) {
        // Scale coordinates to maintain pattern consistency regardless of image size
        double scaleX = (double) referenceSize / width;
        double scaleY = (double) referenceSize / height;

        // Apply the scale factors to the coordinates
        double scaledX = x * scale * scaleX;
        double scaledY = y * scale * scaleY;

        return (float) module.get(scaledX, scaledY);
    }

    /**
     * Get noise value at the specified coordinates using the default reference size
     */
    public float getValue(int x, int y) {
        return getValue(x, y, referenceSize, referenceSize);
    }

    /**
     * Set the reference size used for pattern scaling
     * @param size The reference size
     */
    public void setReferenceSize(int size) {
        this.referenceSize = size;
    }

    /**
     * Get the travel cost at the specified coordinates
     * Higher noise values (whiter) = lower cost
     * Lower noise values (darker) = higher cost
     */
    public float getTravelCost(int x, int y, int width, int height) {
        float noiseValue = getValue(x, y, width, height);

        // Invert the noise value to get the cost (1 = lowest cost, 0 = highest cost)
        // You can adjust this formula to change how the noise affects the cost
        return 1.0f - noiseValue;
    }

    /**
     * Generate a river path from one side to another using A* pathfinding
     * @param width The width of the map
     * @param height The height of the map
     * @param startSide The side to start from (0=top, 1=right, 2=bottom, 3=left)
     * @param endSide The side to end at (0=top, 1=right, 2=bottom, 3=left)
     */
    public void generateRiverPath(int width, int height, int startSide, int endSide) {
        // Clear any existing path
        riverPath.clear();

        // Generate start and end points on the specified sides
        Point start = getRandomPointOnSide(width, height, startSide);
        Point end = getRandomPointOnSide(width, height, endSide);

        // Find the path using A* algorithm
        List<Point> path = findPath(start, end, width, height);

        // Store the path
        if (path != null) {
            riverPath.addAll(path);
            Gdx.app.log("RiverModule", "River path generated with " + path.size() + " points");
        } else {
            Gdx.app.log("RiverModule", "Failed to generate river path");
        }
    }

    /**
     * Check if a point is part of the river path
     */
    public boolean isRiverPath(int x, int y) {
        return riverPath.contains(new Point(x, y));
    }

    /**
     * Get a random point on one of the sides of the map
     */
    private Point getRandomPointOnSide(int width, int height, int side) {
        switch (side) {
            case 0: // Top
                return new Point(random.nextInt(width), 0);
            case 1: // Right
                return new Point(width - 1, random.nextInt(height));
            case 2: // Bottom
                return new Point(random.nextInt(width), height - 1);
            case 3: // Left
                return new Point(0, random.nextInt(height));
            default:
                return new Point(0, 0);
        }
    }

    /**
     * Find a path from start to end using A* pathfinding algorithm
     */
    private List<Point> findPath(Point start, Point end, int width, int height) {
        // Priority queue for open nodes, sorted by f-score
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fScore));

        // Set of visited nodes
        Set<Point> closedSet = new HashSet<>();

        // Map to store the best path to each node
        Map<Point, Point> cameFrom = new HashMap<>();

        // Map to store g-scores (cost from start to current node)
        Map<Point, Double> gScore = new HashMap<>();

        // Initialize with start node
        Node startNode = new Node(start, 0, heuristic(start, end));
        openSet.add(startNode);
        gScore.put(start, 0.0);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            // If we've reached the end, reconstruct and return the path
            if (current.point.equals(end)) {
                return reconstructPath(cameFrom, current.point);
            }

            closedSet.add(current.point);

            // Check all 8 neighbors
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue; // Skip the current point

                    Point neighbor = new Point(current.point.x + dx, current.point.y + dy);

                    // Skip if out of bounds
                    if (neighbor.x < 0 || neighbor.x >= width || neighbor.y < 0 || neighbor.y >= height) {
                        continue;
                    }

                    // Skip if already evaluated
                    if (closedSet.contains(neighbor)) {
                        continue;
                    }

                    // Calculate movement cost (diagonal movement costs more)
                    double moveCost = (dx == 0 || dy == 0) ? 1.0 : 1.414;

                    // Add the terrain cost
                    moveCost *= (1.0 + 10.0 * getTravelCost(neighbor.x, neighbor.y, width, height));

                    // Calculate tentative g-score
                    double tentativeGScore = gScore.getOrDefault(current.point, Double.MAX_VALUE) + moveCost;

                    // If this path is better than any previous one, record it
                    if (tentativeGScore < gScore.getOrDefault(neighbor, Double.MAX_VALUE)) {
                        cameFrom.put(neighbor, current.point);
                        gScore.put(neighbor, tentativeGScore);

                        double fScore = tentativeGScore + heuristic(neighbor, end);
                        openSet.add(new Node(neighbor, tentativeGScore, fScore));
                    }
                }
            }
        }

        // No path found
        return null;
    }

    /**
     * Heuristic function for A* (Euclidean distance)
     */
    private double heuristic(Point a, Point b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    /**
     * Reconstruct the path from the cameFrom map
     */
    private List<Point> reconstructPath(Map<Point, Point> cameFrom, Point current) {
        List<Point> path = new ArrayList<>();
        path.add(current);

        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);
        }

        return path;
    }

    /**
     * Saves the noise as a grayscale image with the river path overlaid.
     * @param filePath The path where the image will be saved
     * @param width The width of the image
     * @param height The height of the image
     */
    public void saveRiverPathImage(String filePath, int width, int height) {
        // Create a new pixmap with the specified dimensions
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        // Generate noise values and set pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get noise value at this position with size-aware scaling
                float noiseValue = getValue(x, y, width, height);

                // Convert to grayscale color (0-255)
                int color = (int)(noiseValue * 255);

                // Set the pixel color (RGBA)
                pixmap.setColor(color/255f, color/255f, color/255f, 1);
                pixmap.drawPixel(x, y);
            }
        }

        // Draw the river path in blue
        pixmap.setColor(0, 0, 1, 1);
        for (Point p : riverPath) {
            pixmap.drawPixel(p.x, p.y);

            // Make the river thicker by drawing adjacent pixels
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = p.x + dx;
                    int ny = p.y + dy;
                    if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                        pixmap.drawPixel(nx, ny);
                    }
                }
            }
        }

        // Save the pixmap to a PNG file
        FileHandle fileHandle = Gdx.files.local(filePath);
        PixmapIO.writePNG(fileHandle, pixmap);

        // Dispose the pixmap to free memory
        pixmap.dispose();

        Gdx.app.log("RiverModule", "River path image saved to: " + filePath);
    }

    /**
     * Saves the noise as a grayscale image.
     * @param filePath The path where the image will be saved
     * @param width The width of the image
     * @param height The height of the image
     */
    public void saveNoiseAsImage(String filePath, int width, int height) {
        // Create a new pixmap with the specified dimensions
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        // Generate noise values and set pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get noise value at this position with size-aware scaling
                float noiseValue = getValue(x, y, width, height);

                // Convert to grayscale color (0-255)
                int color = (int)(noiseValue * 255);

                // Set the pixel color (RGBA)
                pixmap.setColor(color/255f, color/255f, color/255f, 1);
                pixmap.drawPixel(x, y);
            }
        }

        // Save the pixmap to a PNG file
        FileHandle fileHandle = Gdx.files.local(filePath);
        PixmapIO.writePNG(fileHandle, pixmap);

        // Dispose the pixmap to free memory
        pixmap.dispose();

        Gdx.app.log("RiverModule", "Noise image saved to: " + filePath);
    }

    /**
     * Saves the noise as a 1024x1024 grayscale image.
     * @param filePath The path where the image will be saved
     */
    public void saveNoiseAsImage(String filePath) {
        saveNoiseAsImage(filePath, 1024, 1024);
    }

    /**
     * Point class for representing coordinates
     */
    private static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    /**
     * Node class for A* pathfinding
     */
    private static class Node {
        Point point;
        double gScore; // Cost from start to this node
        double fScore; // gScore + heuristic

        Node(Point point, double gScore, double fScore) {
            this.point = point;
            this.gScore = gScore;
            this.fScore = fScore;
        }
    }
}
