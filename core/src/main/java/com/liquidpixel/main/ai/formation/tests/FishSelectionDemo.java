package com.liquidpixel.main.ai.formation.tests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class FishSelectionDemo extends ApplicationAdapter {
    private static final int WORLD_WIDTH = 800;
    private static final int WORLD_HEIGHT = 600;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Texture fishTexture;
    private Array<Fish> fishes;
    private ShaderProgram outlineShader;
    private ShaderProgram defaultShader;
    private Fish selectedFish;

    @Override
    public void create() {
        // Setup camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

        // Setup batch and textures
        batch = new SpriteBatch();
        fishTexture = new Texture(Gdx.files.internal("assets/raw/sprites/64x64/body/01body/fbas_01body_human_00_00.png"));

        // Create shader program
        ShaderProgram.pedantic = false;

        // Load shaders from files
        String vertexShaderPath = "assets/shaders/selection/default.vert";
        String fragmentShaderPath = "assets/shaders/selection/outline.frag";

        outlineShader = new ShaderProgram(
            Gdx.files.internal(vertexShaderPath),
            Gdx.files.internal(fragmentShaderPath)
        );

        if (!outlineShader.isCompiled()) {
            Gdx.app.error("Shader", "Outline shader compilation failed: " + outlineShader.getLog());
        }

        defaultShader = SpriteBatch.createDefaultShader();

        // Create fish
        fishes = new Array<>();
        for (int i = 0; i < 5; i++) {
            float x = MathUtils.random(50, WORLD_WIDTH - 100);
            float y = MathUtils.random(50, WORLD_HEIGHT - 100);
            Fish fish = new Fish(fishTexture, x, y);
            fishes.add(fish);
        }

        // Setup input handling
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Convert screen coordinates to world coordinates
                Vector3 worldCoords = viewport.unproject(new Vector3(screenX, screenY, 0));

                // Check if any fish is clicked
                selectedFish = null;
                for (Fish fish : fishes) {
                    if (fish.getBounds().contains(worldCoords.x, worldCoords.y)) {
                        selectedFish = fish;
                        break;
                    }
                }

                return true;
            }
        });
    }

    @Override
    public void render() {
        // Clear screen
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Begin drawing
        batch.begin();

        // Draw unselected fish with default shader
        batch.setShader(defaultShader);
        for (Fish fish : fishes) {
            if (fish != selectedFish) {
                fish.draw(batch);
            }
        }

        if (selectedFish != null) {
            batch.setShader(outlineShader);
            outlineShader.setUniformf("u_outlineWidth", 2.0f);
            outlineShader.setUniformf("u_outlineColor", 1f, 1f, 1f, 1f);
            outlineShader.setUniformf("u_textureSize", fishTexture.getWidth(), fishTexture.getHeight());
            selectedFish.draw(batch);
        }

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
        fishTexture.dispose();
        outlineShader.dispose();
        defaultShader.dispose();
    }

    // Fish class
    public static class Fish {
        private TextureRegion region;
        private Vector2 position;
        private float width;
        private float height;

        public Fish(Texture texture, float x, float y) {
            this.region = new TextureRegion(texture);
            this.position = new Vector2(x, y);
            this.width = texture.getWidth();
            this.height = texture.getHeight();
        }

        public void draw(SpriteBatch batch) {
            batch.draw(region, position.x, position.y);
        }

        public Rectangle getBounds() {
            return new Rectangle(position.x, position.y, width, height);
        }
    }
}
