package com.liquidpixel.main.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.ai.pathfinding.river.RiverPathfinder;
import com.liquidpixel.main.engine.GameState;
import com.liquidpixel.main.interfaces.INoiseGenerator;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.modules.noise.RiverNoise;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Main module for river generation and application to the world map
 */
public class RiverModule {
    private INoiseGenerator noiseGenerator;
    private RiverPathfinder pathfinder;
    private Random random;
    private List<GridPoint2> riverPath;

    public RiverModule() {
        this.random = new Random(GameState.getSeed());
        this.noiseGenerator = new RiverNoise();
        this.pathfinder = new RiverPathfinder(noiseGenerator);
    }

    /**
     * Generate a river path from one side to another
     *
     * @param width Width of the map
     * @param height Height of the map
     * @param startSide Side to start from (0=top, 1=right, 2=bottom, 3=left)
     * @param endSide Side to end at (0=top, 1=right, 2=bottom, 3=left)
     * @param costMultiplier How much to multiply the cost by (higher = more influence of noise)
     */
    public void generateRiverPath(int width, int height, int startSide, int endSide, float costMultiplier) {
        riverPath = pathfinder.generateRiverPath(width, height, startSide, endSide, costMultiplier);
    }

    /**
     * Apply the generated river to the world map
     *
     * @param worldMap The world map to apply the river to
     * @param terrainType The terrain type to use for the river (e.g., "shallow-water")
     * @param minRiverWidth Minimum width of the river in tiles
     * @param maxRiverWidth Maximum width of the river in tiles
     * @return Number of water tiles created
     */
    public int applyRiverToWorldMap(IWorldMap worldMap, String terrainType, int minRiverWidth, int maxRiverWidth) {
        if (riverPath == null || riverPath.isEmpty()) {
            Gdx.app.error("RiverModule", "No river path generated. Call generateRiverPath first.");
            return 0;
        }

        // Track which points we've already processed to avoid duplicates
        Set<GridPoint2> processedPoints = new HashSet<>();

        // Start with random left and right bank positions
        // This allows asymmetrical river banks
        int leftBank = -random.nextInt(maxRiverWidth / 2) - 1;  // Negative value
        int rightBank = random.nextInt(maxRiverWidth / 2) + 1;  // Positive value

        // For each point in the river path
        for (GridPoint2 point : riverPath) {
            // Randomly decide if we should change the left bank (15% chance)
            if (random.nextFloat() < 0.15f) {
                // Randomly decide to move bank inward or outward by 1
                int change = (random.nextBoolean()) ? 1 : -1;
                leftBank += change;

                // Ensure minimum river width is maintained
                if (rightBank - leftBank < minRiverWidth) {
                    leftBank = rightBank - minRiverWidth;
                }

                // Ensure maximum river width is not exceeded
                if (rightBank - leftBank > maxRiverWidth) {
                    leftBank = rightBank - maxRiverWidth;
                }
            }

            // Randomly decide if we should change the right bank (15% chance)
            if (random.nextFloat() < 0.15f) {
                // Randomly decide to move bank inward or outward by 1
                int change = (random.nextBoolean()) ? 1 : -1;
                rightBank += change;

                // Ensure minimum river width is maintained
                if (rightBank - leftBank < minRiverWidth) {
                    rightBank = leftBank + minRiverWidth;
                }

                // Ensure maximum river width is not exceeded
                if (rightBank - leftBank > maxRiverWidth) {
                    rightBank = leftBank + maxRiverWidth;
                }
            }

            // Apply water to points between the left and right banks
            for (int dx = leftBank; dx <= rightBank; dx++) {
                for (int dy = leftBank; dy <= rightBank; dy++) {
                    // Create a new point with the offset
                    GridPoint2 waterPoint = new GridPoint2(point.x + dx, point.y + dy);

                    // Skip if we've already processed this point
                    if (processedPoints.contains(waterPoint)) {
                        continue;
                    }

                    // Mark as processed
                    processedPoints.add(new GridPoint2(waterPoint.x, waterPoint.y));

                    // Update the terrain to water, but only if the point is within the world bounds
                    if (worldMap.isInBounds(waterPoint)) {
                        worldMap.updateTerrain(waterPoint, terrainType);
                    }
                }
            }
        }

        Gdx.app.log("RiverModule", "Applied river to world map with " +
            processedPoints.size() + " water cells");

        return processedPoints.size();
    }

    /**
     * Save the noise pattern as an image for debugging
     *
     * @param filePath Path to save the image
     * @param width Width of the image
     * @param height Height of the image
     */
    public void saveNoiseAsImage(String filePath, int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float value = noiseGenerator.getValue(x, y, width, height);
                Color color = new Color(value, value, value, 1);
                pixmap.setColor(color);
                pixmap.drawPixel(x, y);
            }
        }

        FileHandle fileHandle = Gdx.files.local(filePath);
        PixmapIO.writePNG(fileHandle, pixmap);
        pixmap.dispose();

        Gdx.app.log("RiverModule", "Saved noise image to " + filePath);
    }

    /**
     * Save an image showing the river path
     *
     * @param filePath Path to save the image
     * @param width Width of the image
     * @param height Height of the image
     * @param riverWidth Width of the river in pixels for visualization
     */
    public void saveRiverPathImage(String filePath, int width, int height, int riverWidth) {
        if (riverPath == null || riverPath.isEmpty()) {
            Gdx.app.error("RiverModule", "No river path to save. Call generateRiverPath first.");
            return;
        }

        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        // Draw background (noise)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float value = noiseGenerator.getValue(x, y, width, height);
                Color color = new Color(value, value, value, 1);
                pixmap.setColor(color);
                pixmap.drawPixel(x, y);
            }
        }

        // Draw river path
        pixmap.setColor(Color.BLUE);
        for (GridPoint2 point : riverPath) {
            if (point.x >= 0 && point.x < width && point.y >= 0 && point.y < height) {
                pixmap.fillCircle(point.x, point.y, riverWidth);
            }
        }

        FileHandle fileHandle = Gdx.files.local(filePath);
        PixmapIO.writePNG(fileHandle, pixmap);
        pixmap.dispose();

        Gdx.app.log("RiverModule", "Saved river path image to " + filePath);
    }

    /**
     * Get the river path
     *
     * @return List of points that form the river path
     */
    public List<GridPoint2> getRiverPath() {
        return riverPath;
    }

    /**
     * Set the padding size for the pathfinder
     *
     * @param paddingSize Padding size in tiles
     */
    public void setPaddingSize(int paddingSize) {
        pathfinder.setPaddingSize(paddingSize);
    }

    /**
     * Get the noise generator
     *
     * @return The noise generator
     */
    public INoiseGenerator getNoiseGenerator() {
        return noiseGenerator;
    }

    /**
     * Get the river pathfinder
     *
     * @return The river pathfinder
     */
    public RiverPathfinder getPathfinder() {
        return pathfinder;
    }
}
