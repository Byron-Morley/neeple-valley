package com.liquidpixel.main.examples;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Standalone example demonstrating hair color ramp functionality
 * Shows how to render hair sprites with different color ramp indices
 */
public class HairColorRampExample extends ApplicationAdapter {
    private Engine engine;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;

    // Color ramp resources
    private ShaderProgram colorRampShader;
    private Texture hairTexture;
    private Texture hairRampA;
    private Texture hairRampB;

    @Override
    public void create() {
        // Initialize LibGDX components
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1200, 800); // Set camera size to match window
        viewport = new ScreenViewport(camera);

        // Initialize Ashley ECS
        engine = new Engine();

        // Load resources
        loadResources();

        // Create hair entities with different color ramp indices
        createHairEntities();

        // Add rendering system
        engine.addSystem(new HairRenderSystem(batch, camera, colorRampShader, hairRampA, hairRampB));

        System.out.println("Hair Color Ramp Example initialized successfully!");
    }

    private void loadResources() {
        // Load hair sprite texture (using a specific hair style)
        System.out.println("Loading hair texture...");
        hairTexture = new Texture(Gdx.files.internal("assets/raw/sprites/64x64/body/13hair/fbas_13hair_spiky1_00_208.png"));
        hairTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        System.out.println("Hair texture loaded: " + hairTexture.getWidth() + "x" + hairTexture.getHeight());

        // Load color ramp textures
        System.out.println("Loading color ramp textures...");
        hairRampA = new Texture(Gdx.files.internal("core/assets/raw/sprites/ramps/hair_ramp_34.png"));
        hairRampA.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        System.out.println("Hair ramp A loaded: " + hairRampA.getWidth() + "x" + hairRampA.getHeight());

        hairRampB = new Texture(Gdx.files.internal("assets/raw/ramps/hair_ramp_b.png"));
        hairRampB.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        System.out.println("Hair ramp B loaded: " + hairRampB.getWidth() + "x" + hairRampB.getHeight());

        // Load color ramp shader
        System.out.println("Loading color ramp shader...");
        colorRampShader = new ShaderProgram(
            Gdx.files.internal("assets/shaders/color_ramps/color_ramp.vert"),
            Gdx.files.internal("assets/shaders/color_ramps/color_ramp_fixed.frag")
        );

        if (!colorRampShader.isCompiled()) {
            System.err.println("Color ramp shader compilation failed:");
            System.err.println(colorRampShader.getLog());
        } else {
            System.out.println("Color ramp shader compiled successfully!");
            System.out.println("Shader uniforms:");
            for (String uniform : new String[]{"u_texture", "u_colorRamp", "u_rampIndex", "u_time"}) {
                System.out.println("  " + uniform + ": " + colorRampShader.hasUniform(uniform));
            }
        }
    }

    private void createHairEntities() {
        // Create 3 hair sprites: original, ramp A, ramp B

        // Original hair (no ramp)
        Entity originalEntity = new Entity();
        originalEntity.add(new HairPositionComponent(200, 400));
        originalEntity.add(new HairSpriteComponent(new TextureRegion(hairTexture)));
        originalEntity.add(new HairColorRampComponent(0.0f, false));
        engine.addEntity(originalEntity);
        System.out.println("Created original hair entity at (200, 400) - no ramp");

        // Hair with ramp A
        Entity rampAEntity = new Entity();
        rampAEntity.add(new HairPositionComponent(500, 400));
        rampAEntity.add(new HairSpriteComponent(new TextureRegion(hairTexture)));
        rampAEntity.add(new HairColorRampComponent(0.0f, true, "ramp_a"));
        engine.addEntity(rampAEntity);
        System.out.println("Created hair entity at (500, 400) using ramp A");

        // Hair with ramp B
        Entity rampBEntity = new Entity();
        rampBEntity.add(new HairPositionComponent(800, 400));
        rampBEntity.add(new HairSpriteComponent(new TextureRegion(hairTexture)));
        rampBEntity.add(new HairColorRampComponent(0.0f, true, "ramp_b"));
        engine.addEntity(rampBEntity);
        System.out.println("Created hair entity at (800, 400) using ramp B");
    }

    @Override
    public void render() {
        // Clear screen
        Gdx.gl.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update camera
        camera.update();

        // Update ECS
        engine.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        hairTexture.dispose();
        hairRampA.dispose();
        hairRampB.dispose();
        colorRampShader.dispose();
    }
}
