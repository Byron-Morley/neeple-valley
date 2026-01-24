package com.liquidpixel.main.model.render;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Shader {
    private ShaderProgram shaderProgram;

    public Shader(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    public void setShaderProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }
}
