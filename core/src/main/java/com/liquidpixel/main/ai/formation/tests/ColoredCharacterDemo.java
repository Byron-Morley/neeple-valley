package com.liquidpixel.main.ai.formation.tests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ColoredCharacterDemo extends ApplicationAdapter {
    private static final int VIEWPORT_WIDTH = 800;
    private static final int VIEWPORT_HEIGHT = 600;
    private static final int SPRITE_SIZE = 64;

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;

    // Character part textures
    private Texture bodyTexture;
    private Texture shirtTexture;
    private Texture pantsTexture;

    // Utility classes
    private SpriteCompositor spriteCompositor;
    private ShaderProcessor shaderProcessor;

    // Display sprites
    private Sprite originalCharacter;
    private Sprite redShirtCharacter;
    private Sprite blueShirtCharacter;
    private Sprite greenShirtCharacter;

    // Simple color change shader
    private static final String VERTEX_SHADER =
        "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
        "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" +
        "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +
        "uniform mat4 u_projTrans;\n" +
        "varying vec4 v_color;\n" +
        "varying vec2 v_texCoords;\n" +
        "\n" +
        "void main() {\n" +
        "    v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" +
        "    v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +
        "    gl_Position = u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
        "}";

    private static final String FRAGMENT_SHADER =
        "#ifdef GL_ES\n" +
        "precision mediump float;\n" +
        "#endif\n" +
        "varying vec4 v_color;\n" +
        "varying vec2 v_texCoords;\n" +
        "uniform sampler2D u_texture;\n" +
        "uniform vec3 u_colorChange;\n" +
        "uniform float u_colorStrength;\n" +
        "\n" +
        "void main() {\n" +
        "    vec4 texColor = texture2D(u_texture, v_texCoords);\n" +
        "    \n" +
        "    // Only modify pixels that have some opacity\n" +
        "    if (texColor.a > 0.0) {\n" +
        "        // Calculate perceived brightness of the original pixel\n" +
        "        float brightness = (texColor.r + texColor.g + texColor.b) / 3.0;\n" +
        "        \n" +
        "        // Create new color by preserving brightness but changing the hue\n" +
        "        vec3 newColor = u_colorChange * brightness;\n" +
        "        \n" +
        "        // Mix between original and new color based on strength\n" +
        "        texColor.rgb = mix(texColor.rgb, newColor, u_colorStrength);\n" +
        "    }\n" +
        "    \n" +
        "    gl_FragColor = texColor * v_color;\n" +
        "}";

    @Override
    public void create() {
        // Set up camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

        // Set up utilities
        spriteCompositor = new SpriteCompositor(SPRITE_SIZE, SPRITE_SIZE);
        shaderProcessor = new ShaderProcessor(VERTEX_SHADER, FRAGMENT_SHADER, SPRITE_SIZE);

        // Create SpriteBatch for rendering
        batch = new SpriteBatch();

        // Load textures with nearest filtering for pixel-perfect rendering
        bodyTexture = new Texture(Gdx.files.internal("assets/raw/sprites/64x64/body/01body/fbas_01body_human_00_00.png"));
        bodyTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        shirtTexture = new Texture(Gdx.files.internal("assets/raw/sprites/64x64/body/05shrt/fbas_05shrt_longshirt_00a_00.png"));
        shirtTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        pantsTexture = new Texture(Gdx.files.internal("assets/raw/sprites/64x64/body/06lwr2/fbas_06lwr2_overalls_00a_00.png"));
        pantsTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Create characters with different colored shirts
        createCharacters();
    }

    /**
     * Creates the character sprites with different colored shirts
     */
    private void createCharacters() {
        // Create original character (no shader)
        originalCharacter = createCharacterWithShirtColor(null, null, 0);

        // Create character with red shirt
        redShirtCharacter = createCharacterWithShirtColor(new float[]{1.0f, 0.0f, 0.0f}, "Red", 0.8f);

        greenShirtCharacter = createCharacterWithShirtColor(new float[]{0.0f, 1.0f, 0.0f}, "Green", 0.8f);
        // Create character with blue shirt
        blueShirtCharacter = createCharacterWithShirtColor(new float[]{0.0f, 0.0f, 1.0f}, "Blue", 0.8f);

        // Create character with green shirt
    }

    /**
     * Creates a character with the shirt colored using the specified color values
     *
     * @param colorValues RGB values for the color (null for original color)
     * @param colorName Name of the color for logging (null for original)
     * @param colorStrength Strength of the color change (0-1)
     * @return A sprite of the full character with the colored shirt
     */
    private Sprite createCharacterWithShirtColor(float[] colorValues, String colorName, float colorStrength) {
        // Create sprites for each body part
        Sprite bodySprite = new Sprite(bodyTexture);
        bodySprite.setSize(SPRITE_SIZE, SPRITE_SIZE);
        bodySprite.setPosition(0, 0);

        Sprite pantsSprite = new Sprite(pantsTexture);
        pantsSprite.setSize(SPRITE_SIZE, SPRITE_SIZE);
        pantsSprite.setPosition(0, 0);

        // Create the shirt sprite - either original or shader-processed
        Sprite shirtSprite;

        if (colorValues != null) {
            Gdx.app.log("Character Creation", "Creating character with " + colorName + " shirt");
            // Apply shader to shirt texture
            shirtSprite = shaderProcessor.applyShaderToTexture(shirtTexture, colorValues, colorStrength);
        } else {
            Gdx.app.log("Character Creation", "Creating character with original shirt");
            // Use original shirt texture
            shirtSprite = new Sprite(shirtTexture);
        }

        shirtSprite.setSize(SPRITE_SIZE, SPRITE_SIZE);
        shirtSprite.setPosition(0, 0);

        // Create array of sprites in the correct drawing order (back to front)
        Array<Sprite> characterParts = new Array<>();
        characterParts.add(bodySprite);     // Body first (bottom layer)
        characterParts.add(pantsSprite);    // Pants second
        characterParts.add(shirtSprite);    // Shirt on top

        // Composite all parts into a single sprite
        return spriteCompositor.compositeSprites(characterParts);
    }

    @Override
    public void render() {
        // Update camera
        camera.update();

        // Clear screen
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Enable blending for proper transparency
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Render sprites
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        int scale = 3; // Scale factor for display

        // Draw original character (top left)
        float x1 = VIEWPORT_WIDTH / 4 - (SPRITE_SIZE * scale) / 2;
        float y1 = VIEWPORT_HEIGHT * 3/4 - (SPRITE_SIZE * scale) / 2;
        drawCharacter(originalCharacter, x1, y1, scale);

        // Draw red shirt character (top right)
        float x2 = VIEWPORT_WIDTH * 3/4 - (SPRITE_SIZE * scale) / 2;
        float y2 = VIEWPORT_HEIGHT * 3/4 - (SPRITE_SIZE * scale) / 2;
        drawCharacter(redShirtCharacter, x2, y2, scale);

        // Draw blue shirt character (bottom left)
        float x3 = VIEWPORT_WIDTH / 4 - (SPRITE_SIZE * scale) / 2;
        float y3 = VIEWPORT_HEIGHT / 4 - (SPRITE_SIZE * scale) / 2;
        drawCharacter(blueShirtCharacter, x3, y3, scale);

        // Draw green shirt character (bottom right)
        float x4 = VIEWPORT_WIDTH * 3/4 - (SPRITE_SIZE * scale) / 2;
        float y4 = VIEWPORT_HEIGHT / 4 - (SPRITE_SIZE * scale) / 2;
        drawCharacter(greenShirtCharacter, x4, y4, scale);

        batch.end();
    }

    /**
     * Helper method to draw a character sprite at a specific position with scaling
     */
    private void drawCharacter(Sprite sprite, float x, float y, int scale) {
        sprite.setPosition(x, y);
        sprite.setSize(SPRITE_SIZE * scale, SPRITE_SIZE * scale);
        sprite.draw(batch);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }

    @Override
    public void dispose() {
        batch.dispose();
        bodyTexture.dispose();
        shirtTexture.dispose();
        pantsTexture.dispose();
        spriteCompositor.dispose();
        shaderProcessor.dispose();
    }

    /**
     * A utility class that composites multiple sprites into a single sprite using a FrameBuffer
     */
    public static class SpriteCompositor implements Disposable {
        private final int width;
        private final int height;
        private final FrameBuffer frameBuffer;
        private final SpriteBatch batch;
        private final OrthographicCamera camera;

        /**
         * Creates a new SpriteCompositor
         * @param width The width of the composited sprite
         * @param height The height of the composited sprite
         */
        public SpriteCompositor(int width, int height) {
            this.width = width;
            this.height = height;

            // Create a camera specifically for the framebuffer
            camera = new OrthographicCamera();
            camera.setToOrtho(false, width, height);
            camera.update();

            // Create a framebuffer for compositing
            frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);

            // Create a SpriteBatch for drawing to the framebuffer
            batch = new SpriteBatch();
        }

        /**
         * Composites multiple sprites into a single sprite
         * @param sprites The sprites to composite (will be drawn in order)
         * @return A new sprite containing the composited image
         */
        public Sprite compositeSprites(Array<Sprite> sprites) {
            // Begin drawing to the framebuffer
            frameBuffer.begin();

            // Clear the framebuffer
            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // Enable blending for proper transparency
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            // Set up the batch for the framebuffer
            batch.setProjectionMatrix(camera.combined);
            batch.begin();

            // Draw all sprites to the framebuffer
            for (Sprite sprite : sprites) {
                sprite.draw(batch);
            }
            batch.end();
            frameBuffer.end();

            // Create a TextureRegion from the framebuffer's texture
            TextureRegion region = new TextureRegion(frameBuffer.getColorBufferTexture());
            region.flip(false, true); // Flip Y-axis since framebuffer is upside down

            // Create and return a new sprite from the composited texture
            Sprite result = new Sprite(region);

            // Make sure the result texture uses nearest filtering
            result.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            return result;
        }

        @Override
        public void dispose() {
            frameBuffer.dispose();
            batch.dispose();
        }
    }

    /**
     * A utility class that applies shaders to textures
     */
    public static class ShaderProcessor implements Disposable {
        private final ShaderProgram shader;
        private final FrameBuffer frameBuffer;
        private final int size;

        /**
         * Creates a new ShaderProcessor
         *
         * @param vertexShader The vertex shader code
         * @param fragmentShader The fragment shader code
         * @param size The size of the texture to process
         */
        public ShaderProcessor(String vertexShader, String fragmentShader, int size) {
            this.size = size;

            // Set up shader
            ShaderProgram.pedantic = false;
            shader = new ShaderProgram(vertexShader, fragmentShader);
            if (!shader.isCompiled()) {
                Gdx.app.error("Shader", "Compilation failed:\n" + shader.getLog());
            } else {
                Gdx.app.log("Shader", "Shader compiled successfully");
            }

            // Create framebuffer for shader application
            frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, size, size, false);
        }

        /**
         * Applies a color shader to a texture and returns the result as a new sprite
         *
         * @param inputTexture The texture to apply the shader to
         * @param colorValues RGB values for the color change
         * @param colorStrength Strength of the color change (0-1)
         * @return A new sprite with the shader applied
         */
        public Sprite applyShaderToTexture(Texture inputTexture, float[] colorValues, float colorStrength) {
            // Begin drawing to the framebuffer
            frameBuffer.begin();

            // Clear the framebuffer with transparent black
            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // Enable blending for proper alpha handling
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            // Create a temporary camera for the framebuffer
            OrthographicCamera fbCamera = new OrthographicCamera(size, size);
            fbCamera.position.set(size/2, size/2, 0);
            fbCamera.update();

            // Create a new SpriteBatch for the framebuffer
            SpriteBatch fbBatch = new SpriteBatch();
            fbBatch.setProjectionMatrix(fbCamera.combined);

            // Set the shader and begin drawing
            fbBatch.setShader(shader);
            fbBatch.begin();

            // Set shader parameters
            shader.setUniform3fv("u_colorChange", colorValues, 0, 3);
            shader.setUniformf("u_colorStrength", colorStrength);

            // Create and draw a sprite from the input texture
            Sprite tempSprite = new Sprite(inputTexture);
            tempSprite.setPosition(0, 0);
            tempSprite.setSize(size, size);
            tempSprite.draw(fbBatch);

            // Finish drawing
            fbBatch.end();
            fbBatch.dispose();

            // End the framebuffer
            frameBuffer.end();

            // Create a texture region from the framebuffer
            TextureRegion region = new TextureRegion(frameBuffer.getColorBufferTexture());
            region.flip(false, true); // FBO y-axis is flipped

            // Make sure the result texture uses nearest filtering
            Texture resultTexture = region.getTexture();
            resultTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            // Create and return a sprite with the processed texture
            return new Sprite(region);
        }

        @Override
        public void dispose() {
            shader.dispose();
            frameBuffer.dispose();
        }
    }
}
