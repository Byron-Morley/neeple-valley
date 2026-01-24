package com.liquidpixel.main.examples;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Rendering system for hair sprites with color ramp support
 */
public class HairRenderSystem extends IteratingSystem {
    private ComponentMapper<HairPositionComponent> positionMapper;
    private ComponentMapper<HairSpriteComponent> spriteMapper;
    private ComponentMapper<HairColorRampComponent> colorRampMapper;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ShaderProgram colorRampShader;
    private Texture hairRampA;
    private Texture hairRampB;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    public HairRenderSystem(SpriteBatch batch, OrthographicCamera camera,
                           ShaderProgram colorRampShader, Texture hairRampA, Texture hairRampB) {
        super(Family.all(HairPositionComponent.class, HairSpriteComponent.class, HairColorRampComponent.class).get());

        this.batch = batch;
        this.camera = camera;
        this.colorRampShader = colorRampShader;
        this.hairRampA = hairRampA;
        this.hairRampB = hairRampB;
        this.shapeRenderer = new ShapeRenderer();
        this.font = new BitmapFont();

        // Initialize component mappers
        positionMapper = ComponentMapper.getFor(HairPositionComponent.class);
        spriteMapper = ComponentMapper.getFor(HairSpriteComponent.class);
        colorRampMapper = ComponentMapper.getFor(HairColorRampComponent.class);
    }

    @Override
    public void update(float deltaTime) {
        // Begin batch with camera projection
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Process all hair entities
        super.update(deltaTime);

        // Reset shader and end batch
        batch.setShader(null);
        batch.end();

        // Draw ramp color visualization
//        drawRampColorVisualization();
    }

    private void drawRampColorVisualization() {
        // Sample colors from both ramp textures and display them
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Sample colors from ramp A
        if (!hairRampA.getTextureData().isPrepared()) {
            hairRampA.getTextureData().prepare();
        }
        Pixmap pixmapA = hairRampA.getTextureData().consumePixmap();

        // Draw ramp A colors (6 colors across)
        for (int i = 0; i < 6; i++) {
            int x = 1 + i * 2; // Center of each 2x2 color block
            Color colorA = new Color(pixmapA.getPixel(x, 1));
            shapeRenderer.setColor(colorA);
            shapeRenderer.rect(50 + i * 40, 50, 30, 30);
        }
        pixmapA.dispose();

        // Sample colors from ramp B
        if (!hairRampB.getTextureData().isPrepared()) {
            hairRampB.getTextureData().prepare();
        }
        Pixmap pixmapB = hairRampB.getTextureData().consumePixmap();

        // Draw ramp B colors (6 colors across)
        for (int i = 0; i < 6; i++) {
            int x = 1 + i * 2; // Center of each 2x2 color block
            Color colorB = new Color(pixmapB.getPixel(x, 1));
            shapeRenderer.setColor(colorB);
            shapeRenderer.rect(50 + i * 40, 90, 30, 30);
        }
        pixmapB.dispose();

        shapeRenderer.end();

        // Draw labels
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "Ramp A Colors", 50, 40);
        font.draw(batch, "Ramp B Colors", 50, 140);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HairPositionComponent position = positionMapper.get(entity);
        HairSpriteComponent sprite = spriteMapper.get(entity);
        HairColorRampComponent colorRamp = colorRampMapper.get(entity);

        if (colorRamp.enabled && colorRamp.rampName != null) {
            // Use color ramp shader
            if (colorRampShader.isCompiled()) {
                batch.setShader(colorRampShader);

                // Set shader uniforms first
                if (colorRampShader.hasUniform("u_texture")) {
                    colorRampShader.setUniformi("u_texture", 0);
                }
                if (colorRampShader.hasUniform("u_colorRamp")) {
                    colorRampShader.setUniformi("u_colorRamp", 1);
                }
                if (colorRampShader.hasUniform("u_rampIndex")) {
                    colorRampShader.setUniformf("u_rampIndex", colorRamp.rampIndex);
                }
                if (colorRampShader.hasUniform("u_time")) {
                    colorRampShader.setUniformf("u_time", 0.0f);
                }

                // Flush the batch to ensure shader is applied
                batch.flush();

                // Now bind the correct ramp texture to texture unit 1
                Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1);
                if (colorRamp.rampName.equals("ramp_a")) {
                    hairRampA.bind();
                } else if (colorRamp.rampName.equals("ramp_b")) {
                    hairRampB.bind();
                }

                // Switch back to texture unit 0 for the main sprite texture
                Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

            } else {
                // Fallback to default shader if color ramp shader failed
                batch.setShader(null);
            }
        } else {
            // Use default shader
            batch.setShader(null);
        }

        // Set render color
        batch.setColor(Color.WHITE);

        // Draw the hair sprite (scaled up 3x for better visibility)
        TextureRegion hairRegion = sprite.textureRegion;
        float scale = 3.0f;

        batch.draw(hairRegion,
                   position.x, position.y,
                   0, 0, // origin
                   hairRegion.getRegionWidth() * scale,
                   hairRegion.getRegionHeight() * scale,
                   1.0f, 1.0f, // scale (already applied above)
                   0); // rotation
    }
}
