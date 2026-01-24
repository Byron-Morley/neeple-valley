package com.liquidpixel.main.modules;

import com.badlogic.gdx.Gdx;
import com.liquidpixel.main.engine.GameState;
import com.liquidpixel.main.interfaces.INoiseModule;
import com.liquidpixel.main.interfaces.services.ITerrainService;
import com.liquidpixel.main.model.terrain.TerrainDefinition;
import com.liquidpixel.main.model.terrain.TerrainItem;
import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleScaleDomain;

import java.util.Random;

import static com.sudoplay.joise.module.ModuleBasisFunction.BasisType.GRADIENT;
import static com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType.CUBIC;
import static com.sudoplay.joise.module.ModuleFractal.FractalType.FBM;

public class ChunkModule implements INoiseModule {
    final Random random;
    Module module;
    ITerrainService terrainService;

    public ChunkModule(ITerrainService terrainService) {
        random = new Random();
        this.terrainService = terrainService;
        module = getFractalModule(GRADIENT, CUBIC, FBM);
    }

    private ModuleAutoCorrect getFractalModule(
        ModuleBasisFunction.BasisType basisType,
        ModuleBasisFunction.InterpolationType interpolationType,
        ModuleFractal.FractalType fractalType
    ) {
        ModuleFractal gen = new ModuleFractal();
        gen.setAllSourceBasisTypes(basisType);
        gen.setAllSourceInterpolationTypes(interpolationType);
        gen.setNumOctaves(5);
        gen.setFrequency(2.34);
        gen.setType(fractalType);
        gen.setSeed(GameState.getSeed());

        ModuleScaleDomain moduleScaleDomain = new ModuleScaleDomain();
        moduleScaleDomain.setSource(gen);
        moduleScaleDomain.setScaleX(0.5);
        moduleScaleDomain.setScaleY(0.5);

        ModuleAutoCorrect source = new ModuleAutoCorrect(0, 1);
        source.setSource(moduleScaleDomain);
        source.setSamples(10000);
        source.calculate2D();
        return source;
    }

    public TerrainDefinition getTerrain(int x, int y) {
        float tileValue = calculateTileValue(x, y);
        return terrainService.getTerrainType(tileValue);
    }

    @Override
    public int getVariant(TerrainDefinition terrain) {
        return terrainService.getVariant(terrain);
    }

    @Override
    public TerrainItem getItem(TerrainDefinition terrain) {
        return terrainService.getItem(terrain);
    }

    public float calculateTileValue(int x, int y) {
        float px = x / 128f * 1;
        float py = y / 128f * 1;
        float tileValue = (float) module.get(px, py);
        return Math.max(0, Math.min(1, tileValue));
    }
}
