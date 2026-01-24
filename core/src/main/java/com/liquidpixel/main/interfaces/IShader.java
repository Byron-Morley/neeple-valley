package com.liquidpixel.main.interfaces;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.liquidpixel.main.systems.render.RenderSystem;

public interface IShader {
    ShaderProgram getShaderProgram();

    void apply(ShaderProgram targetShader, SpriteBatch batch);
}
