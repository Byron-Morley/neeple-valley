package com.liquidpixel.main.model.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.liquidpixel.main.interfaces.IShader;

public class ColorShader extends Shader implements IShader {

    Texture colorRamp;

    public ColorShader(ShaderProgram shaderProgram, Texture colorRamp) {
        super(shaderProgram);
        this.colorRamp = colorRamp;
    }

    public void apply(ShaderProgram targetShader, SpriteBatch batch) {
        if (targetShader != null) {
            targetShader.bind();

            if (targetShader.hasUniform("u_texture")) {
                targetShader.setUniformi("u_texture", 0);
            }
            if (targetShader.hasUniform("u_colorRamp")) {
                targetShader.setUniformi("u_colorRamp", 1);
            }
            if (targetShader.hasUniform("u_rampIndex")) {
                targetShader.setUniformf("u_rampIndex", 0.0f);
            }
            if (targetShader.hasUniform("u_time")) {
                targetShader.setUniformf("u_time", 0.0f);
            }

            batch.flush();

            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1);

            colorRamp.bind();

            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        }
    }
}
