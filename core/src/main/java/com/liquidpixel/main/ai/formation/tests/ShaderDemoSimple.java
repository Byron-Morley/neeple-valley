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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ShaderDemoSimple extends ApplicationAdapter {
    private static final int VIEWPORT_WIDTH = 800;
    private static final int VIEWPORT_HEIGHT = 600;
    private static final int SPRITE_SIZE = 64;

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private Texture originalTexture;

    private Sprite originalSprite;
    private Sprite shadedSprite;

    private ShaderProgram colorShader;
    private FrameBuffer frameBuffer;

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

        // Set up shader with detailed error reporting
        ShaderProgram.pedantic = false;
        colorShader = new ShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        if (!colorShader.isCompiled()) {
            Gdx.app.error("Shader", "Compilation failed:\n" + colorShader.getLog());
        } else {
            Gdx.app.log("Shader", "Shader compiled successfully");
        }

        // Create SpriteBatch for rendering
        batch = new SpriteBatch();

        // Load just the shirt texture
        originalTexture = new Texture(Gdx.files.internal("assets/raw/sprites/64x64/body/05shrt/fbas_05shrt_longshirt_00a_00.png"));
        originalTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Create original sprite
        originalSprite = new Sprite(originalTexture);

        // Create framebuffer for shader application
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, SPRITE_SIZE, SPRITE_SIZE, false);

        // Apply shader to create a red version
        shadedSprite = applyShaderToTexture(originalTexture, new float[]{1.0f, 0.0f, 0.0f}, 0.8f); // Bright red with 80% strength
    }

    /**
     * Applies a color shader to a texture and returns the result as a new sprite
     */
    private Sprite applyShaderToTexture(Texture inputTexture, float[] colorValues, float colorStrength) {
        // Begin drawing to the framebuffer
        frameBuffer.begin();

        // Clear the framebuffer with transparent black
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Enable blending for proper alpha handling
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Create a temporary camera for the framebuffer
        OrthographicCamera fbCamera = new OrthographicCamera(SPRITE_SIZE, SPRITE_SIZE);
        fbCamera.position.set(SPRITE_SIZE/2, SPRITE_SIZE/2, 0);
        fbCamera.update();

        // Create a new SpriteBatch for the framebuffer
        SpriteBatch fbBatch = new SpriteBatch();
        fbBatch.setProjectionMatrix(fbCamera.combined);

        // Set the shader and begin drawing
        fbBatch.setShader(colorShader);
        fbBatch.begin();

        // Set shader parameters
        colorShader.setUniform3fv("u_colorChange", colorValues, 0, 3);
        colorShader.setUniformf("u_colorStrength", colorStrength);

        // Create and draw a sprite from the input texture
        Sprite tempSprite = new Sprite(inputTexture);
        tempSprite.setPosition(0, 0);
        tempSprite.setSize(SPRITE_SIZE, SPRITE_SIZE);
        tempSprite.draw(fbBatch);

        // Finish drawing
        fbBatch.end();
        fbBatch.dispose();

        // End the framebuffer
        frameBuffer.end();

        // Create a texture region from the framebuffer
        TextureRegion region = new TextureRegion(frameBuffer.getColorBufferTexture());
        region.flip(false, true); // FBO y-axis is flipped

        // Get the texture from the region and set nearest filtering
        Texture resultTexture = region.getTexture();
        resultTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Create and return a sprite with the processed texture
        return new Sprite(region);
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

        int scale = 6; // Make sprites larger for better visibility

        // Draw original sprite on the left
        float originalX = VIEWPORT_WIDTH / 4 - (SPRITE_SIZE * scale) / 2;
        float originalY = VIEWPORT_HEIGHT / 2 - (SPRITE_SIZE * scale) / 2;

        originalSprite.setPosition(originalX, originalY);
        originalSprite.setSize(SPRITE_SIZE * scale, SPRITE_SIZE * scale);
        originalSprite.draw(batch);

        // Draw shaded sprite on the right
        float shadedX = 3 * VIEWPORT_WIDTH / 4 - (SPRITE_SIZE * scale) / 2;
        float shadedY = VIEWPORT_HEIGHT / 2 - (SPRITE_SIZE * scale) / 2;

        shadedSprite.setPosition(shadedX, shadedY);
        shadedSprite.setSize(SPRITE_SIZE * scale, SPRITE_SIZE * scale);
        shadedSprite.draw(batch);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }

    @Override
    public void dispose() {
        batch.dispose();
        originalTexture.dispose();
        colorShader.dispose();
        frameBuffer.dispose();
    }
}

