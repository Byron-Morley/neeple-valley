package com.liquidpixel.main.components.render;

import com.badlogic.ashley.core.Component;
import com.liquidpixel.main.interfaces.IShader;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShaderComponent implements Component {

    Map<String, IShader> shaders;

    public ShaderComponent() {
        this.shaders  = new HashMap<>();
    }

    public void addShader(String name, IShader shader) {
        this.shaders.put(name, shader);
    }

    public void addShader(IShader shader) {
        this.shaders.put("default",  shader);
    }

    public IShader getShader(String name) {
        return this.shaders.get(name);
    }
}
