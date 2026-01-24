package com.liquidpixel.main.ai.formation.tests;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

// You'll need to add a YAML parsing library to your project
// For example: com.esotericsoftware:yamlbeans or org.yaml:snakeyaml
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class ShirtColorSwapper extends ApplicationAdapter {
    private static final String SHIRT_PATH = "assets/raw/sprites/64x64/body/05shrt/fbas_05shrt_longshirt_00a_00.png";
    private static final String RAMPS_YAML_PATH = "assets/raw/ramps/color_ramps.yaml"; // Path to your YAML file
    private static final int ZOOM_FACTOR = 4; // Makes the sprite 4x larger
    private static final int BLACK_COLOR = 0x181818FF; // 24,24,24 in RGBA format

    private SpriteBatch batch;
    private Texture originalTexture;
    private Texture alternateTexture;
    private boolean showingOriginal = true;

    // Store the color ramps from YAML
    private Array<String> rampNames = new Array<>();
    private ObjectMap<String, int[][]> colorRamps = new ObjectMap<>();
    private int currentRampIndex = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Load the original texture
        originalTexture = new Texture(SHIRT_PATH);

        // Load color ramps from YAML file
        loadColorRampsFromYaml();

        // Create the alternate color version
        alternateTexture = createAlternateTexture();

        // Set up input handling for switching between textures
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                toggleTexture();
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                toggleTexture();
                return true;
            }
        });
    }

    private void loadColorRampsFromYaml() {
        try {
            FileHandle fileHandle = Gdx.files.internal(RAMPS_YAML_PATH);
            if (!fileHandle.exists()) {
                Gdx.app.error("ShirtColorSwapper", "YAML file not found: " + RAMPS_YAML_PATH);
                return;
            }

            InputStream inputStream = fileHandle.read();
            Yaml yaml = new Yaml();
            Map<String, List<Map<String, Integer>>> ramps = yaml.load(inputStream);

            // Process each ramp in the YAML
            for (String rampName : ramps.keySet()) {
                List<Map<String, Integer>> colors = ramps.get(rampName);

                // Skip empty ramps
                if (colors == null || colors.isEmpty()) {
                    Gdx.app.log("ShirtColorSwapper", "Skipping empty ramp: " + rampName);
                    continue;
                }

                // Convert the colors to an int[][] array (RGB values)
                int[][] rampColors = new int[colors.size()][3];
                for (int i = 0; i < colors.size(); i++) {
                    Map<String, Integer> color = colors.get(i);
                    rampColors[i][0] = color.get("r");
                    rampColors[i][1] = color.get("g");
                    rampColors[i][2] = color.get("b");
                }

                colorRamps.put(rampName, rampColors);
                rampNames.add(rampName);

                Gdx.app.log("ShirtColorSwapper", "Loaded ramp: " + rampName +
                    " with " + rampColors.length + " colors");
            }

        } catch (Exception e) {
            Gdx.app.error("ShirtColorSwapper", "Error loading YAML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void toggleTexture() {
        if (showingOriginal) {
            showingOriginal = false;
            currentRampIndex = 0; // First alternate ramp
        } else {
            currentRampIndex++;
            if (currentRampIndex >= rampNames.size) {
                showingOriginal = true; // Loop back to original
            } else {
                // Create new texture with the selected ramp
                if (alternateTexture != null) {
                    alternateTexture.dispose();
                }
                alternateTexture = createAlternateTexture();
            }
        }

        String statusMessage = showingOriginal ? "original" : "ramp: " + rampNames.get(currentRampIndex);
        Gdx.app.log("ShirtColorSwapper", "Showing " + statusMessage);
    }

    private Texture createAlternateTexture() {
        // Original green ramp RGB values (excluding black)
        int[][] originalRamp = {
            {88, 224, 160},   // Light
            {40, 152, 96},   // Medium
            {32, 80, 64},    // Dark
        };

        // Get the original pixmap
        if (!originalTexture.getTextureData().isPrepared()) {
            originalTexture.getTextureData().prepare();
        }
        Pixmap originalPixmap = originalTexture.getTextureData().consumePixmap();

        // Create a new pixmap with the same dimensions
        Pixmap newPixmap = new Pixmap(
                originalPixmap.getWidth(),
                originalPixmap.getHeight(),
                Pixmap.Format.RGBA8888
        );

        // Current target ramp to use (if we're not showing original)
        String currentRampName = rampNames.get(currentRampIndex);
        int[][] targetRamp = colorRamps.get(currentRampName);

        // Process each pixel
        for (int y = 0; y < originalPixmap.getHeight(); y++) {
            for (int x = 0; x < originalPixmap.getWidth(); x++) {
                int originalColor = originalPixmap.getPixel(x, y);

                // Skip fully transparent pixels
                if ((originalColor & 0xFF) == 0) {
                    newPixmap.drawPixel(x, y, originalColor);
                    continue;
                }

                // Keep the black color the same
                if ((originalColor & 0xFFFFFF00) == (BLACK_COLOR & 0xFFFFFF00)) {
                    newPixmap.drawPixel(x, y, originalColor);
                    continue;
                }

                // Extract alpha component
                int a = originalColor & 0xFF;

                // For the RGB values, find closest match in original ramp
                int r = (originalColor & 0xFF000000) >>> 24;
                int g = (originalColor & 0x00FF0000) >>> 16;
                int b = (originalColor & 0x0000FF00) >>> 8;

                // Find the closest color in the original ramp
                int closestIndex = findClosestColorIndex(r, g, b, originalRamp);

                // If the target ramp has fewer colors than the original, cap the index
                int safeIndex = Math.min(closestIndex, targetRamp.length - 1);

                // Get the target color's RGB values
                int tr = targetRamp[safeIndex][0];
                int tg = targetRamp[safeIndex][1];
                int tb = targetRamp[safeIndex][2];

                // Pack the new color with the original alpha
                int newColor = ((tr & 0xFF) << 24) |
                               ((tg & 0xFF) << 16) |
                               ((tb & 0xFF) << 8) |
                               (a & 0xFF);

                // Set the pixel in the new pixmap
                newPixmap.drawPixel(x, y, newColor);
            }
        }

        // Create a new texture from the modified pixmap
        Texture newTexture = new Texture(newPixmap);

        // Clean up
        originalPixmap.dispose();
        newPixmap.dispose();

        return newTexture;
    }

    private int findClosestColorIndex(int r, int g, int b, int[][] colorRamp) {
        int closestIndex = 0;
        double closestDistance = Double.MAX_VALUE;

        for (int i = 0; i < colorRamp.length; i++) {
            int[] rampColor = colorRamp[i];

            // Calculate Euclidean distance in RGB space
            double distance = Math.sqrt(
                    Math.pow(r - rampColor[0], 2) +
                    Math.pow(g - rampColor[1], 2) +
                    Math.pow(b - rampColor[2], 2));

            if (distance < closestDistance) {
                closestDistance = distance;
                closestIndex = i;
            }
        }

        return closestIndex;
    }

    // Helper method to extract RGB from packed color
    private int[] extractRgb(int packedColor) {
        int r = (packedColor & 0xFF000000) >>> 24;
        int g = (packedColor & 0x00FF0000) >>> 16;
        int b = (packedColor & 0x0000FF00) >>> 8;
        return new int[] {r, g, b};
    }

    @Override
    public void render() {
        // Clear the screen
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);

        // Get the current texture
        Texture currentTexture = showingOriginal ? originalTexture : alternateTexture;

        batch.begin();

        // Draw the texture centered and zoomed
        float scaledWidth = currentTexture.getWidth() * ZOOM_FACTOR;
        float scaledHeight = currentTexture.getHeight() * ZOOM_FACTOR;
        float x = Gdx.graphics.getWidth() / 2f - scaledWidth / 2f;
        float y = Gdx.graphics.getHeight() / 2f - scaledHeight / 2f;

        batch.draw(
            currentTexture,
            x, y,
            scaledWidth, scaledHeight
        );

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        originalTexture.dispose();
        if (alternateTexture != null) {
            alternateTexture.dispose();
        }
    }
}
