package com.liquidpixel.main.modules.noise;

import com.liquidpixel.main.engine.GameState;
import com.liquidpixel.main.interfaces.INoiseGenerator;
import com.sudoplay.joise.module.*;
import com.sudoplay.joise.module.Module;

public class RiverNoise implements INoiseGenerator {
    private Module noiseModule;
    private double scale = 0.1; // Base scale factor
    private int referenceSize = 1024; // Reference size for consistent pattern scaling
    private int scaleMod = 4;

    public RiverNoise() {
        this.noiseModule = createRidgeMultiNoiseModule();
    }

    private Module createRidgeMultiNoiseModule() {
        ModuleFractal gen = new ModuleFractal();
        gen.setAllSourceBasisTypes(ModuleBasisFunction.BasisType.SIMPLEX);
        gen.setAllSourceInterpolationTypes(ModuleBasisFunction.InterpolationType.QUINTIC);
        gen.setNumOctaves(1);
        gen.setFrequency(0.02);
        gen.setType(ModuleFractal.FractalType.RIDGEMULTI);
        gen.setSeed(GameState.getSeed());

        ModuleBias biasModule = new ModuleBias();
        biasModule.setSource(gen);
        biasModule.setBias(0.2);

        ModuleAutoCorrect source = new ModuleAutoCorrect(0, 1);
        source.setSource(biasModule);
        source.setSamples(10000);
        source.calculate2D();

        return source;
    }

    public float getValue(int x, int y, int width, int height) {
        // Scale coordinates to maintain pattern consistency regardless of image size
        double scaleX = (double) referenceSize / width;
        double scaleY = (double) referenceSize / height;

        // Apply the scale factors to the coordinates
        double scaledX = x * scale * scaleX * scaleMod;
        double scaledY = y * scale * scaleY * scaleMod;

        return (float) noiseModule.get(scaledX, scaledY);
    }

    public float getValue(int x, int y) {
        return getValue(x, y, referenceSize, referenceSize);
    }

    public void setReferenceSize(int size) {
        this.referenceSize = size;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }
}
