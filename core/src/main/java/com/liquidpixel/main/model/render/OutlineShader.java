package com.liquidpixel.main.model.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.liquidpixel.main.interfaces.IShader;
import com.liquidpixel.sprite.model.GameSprite;

public class OutlineShader extends Shader implements IShader {

    private float outlineWidth = 2.0f;
    private float[] outlineColor = new float[] {1f, 1f, 1f, 1f}; // Default white
    private float textureWidth;
    private float textureHeight;

    public OutlineShader(ShaderProgram shaderProgram) {
        super(shaderProgram);
    }

    public void setOutlineWidth(float width) {
        this.outlineWidth = width;
    }

    public void setOutlineColor(float r, float g, float b, float a) {
        this.outlineColor[0] = r;
        this.outlineColor[1] = g;
        this.outlineColor[2] = b;
        this.outlineColor[3] = a;
    }

    /**
     * Updates the texture size parameters for the shader.
     * This should be called when we know which sprite is being rendered.
     */
    public void updateTextureSize(GameSprite sprite) {
        if (sprite != null && sprite.getTexture() != null) {
            Texture texture = sprite.getTexture();
            this.textureWidth = texture.getWidth();
            this.textureHeight = texture.getHeight();
        }
    }

    @Override
    public void apply(ShaderProgram targetShader, SpriteBatch batch) {
        if (targetShader != null) {
            targetShader.bind();

            // Set the outline properties
            if (targetShader.hasUniform("u_outlineWidth")) {
                targetShader.setUniformf("u_outlineWidth", outlineWidth);
            }

            if (targetShader.hasUniform("u_outlineColor")) {
                targetShader.setUniformf("u_outlineColor",
                    outlineColor[0], outlineColor[1], outlineColor[2], outlineColor[3]);
            }

            // This is needed for pixel-perfect outline calculation
            if (targetShader.hasUniform("u_textureSize")) {
                targetShader.setUniformf("u_textureSize", textureWidth, textureHeight);
            }

            // Standard uniforms
            if (targetShader.hasUniform("u_texture")) {
                targetShader.setUniformi("u_texture", 0);
            }

            batch.flush();
        }
    }
}
