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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SpriteCompositionDemo extends ApplicationAdapter {
    private static final int VIEWPORT_WIDTH = 800;
    private static final int VIEWPORT_HEIGHT = 600;
    private static final int SPRITE_SIZE = 64;

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private Texture bodyTexture;
    private Texture shirtTexture;
    private Texture pantsTexture;
    private Array<Texture> textures;
    private Sprite compositeSprite;

    // Reusable sprite compositor
    private SpriteCompositor spriteCompositor;

    @Override
    public void create() {
        // Set up camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

        // Create sprite compositor
        spriteCompositor = new SpriteCompositor(SPRITE_SIZE, SPRITE_SIZE);

        // Load textures with nearest filtering for pixel-perfect rendering
        bodyTexture = new Texture(Gdx.files.internal("assets/raw/sprites/64x64/body/01body/fbas_01body_human_00_00.png"));
        bodyTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        shirtTexture = new Texture(Gdx.files.internal("assets/raw/sprites/64x64/body/05shrt/fbas_05shrt_longshirt_00a_00.png"));
        shirtTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        pantsTexture = new Texture(Gdx.files.internal("assets/raw/sprites/64x64/body/06lwr2/fbas_06lwr2_overalls_00a_00.png"));
        pantsTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Create the array of textures to composite
        textures = new Array<>();
        textures.add(bodyTexture);
        textures.add(shirtTexture);
        textures.add(pantsTexture);

        // Create the composite sprite
        compositeSprite = createCompositeSprite();

        // Create SpriteBatch for rendering without blending
        batch = new SpriteBatch();
    }

    /**
     * Creates a composite sprite from the loaded textures
     */
    private Sprite createCompositeSprite() {
        // Create sprites from textures
        Array<Sprite> sprites = new Array<>();
        for (Texture texture : textures) {
            Sprite sprite = new Sprite(texture);
            sprite.setSize(SPRITE_SIZE, SPRITE_SIZE);
            sprite.setPosition(0, 0);
            sprites.add(sprite);
        }

        // Composite the sprites
        return spriteCompositor.compositeSprites(sprites);
    }

    @Override
    public void render() {
        // Update camera
        camera.update();

        // Clear screen
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render the composite sprite to the screen
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Draw the composited sprite in the center of the screen, scaled up for visibility
        // Use integer scaling for pixel-perfect results
        int scale = 4; // Using int instead of float for exact pixel scaling
        float x = Math.round((VIEWPORT_WIDTH - SPRITE_SIZE * scale) / 2);
        float y = Math.round((VIEWPORT_HEIGHT - SPRITE_SIZE * scale) / 2);

        // Set the position and scale of the sprite
        compositeSprite.setPosition(x, y);
        compositeSprite.setSize(SPRITE_SIZE * scale, SPRITE_SIZE * scale);

        // Draw the sprite
        compositeSprite.draw(batch);

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
        bodyTexture.dispose();
        shirtTexture.dispose();
        pantsTexture.dispose();
        spriteCompositor.dispose();
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

            // Make sure the result texture also uses nearest filtering
            result.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            return result;
        }

        @Override
        public void dispose() {
            frameBuffer.dispose();
            batch.dispose();
        }
    }
}
