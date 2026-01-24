package com.liquidpixel.main.generators.procedural;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.liquidpixel.main.model.terrain.TerrainDefinition;

public class Tile extends TiledMapTileLayer.Cell implements TiledMapTile {

    TextureRegion textureRegion;
    TerrainDefinition terrain;

    public Tile(TextureRegion textureRegion, TerrainDefinition terrain) {
        setTile(this);
        this.textureRegion = textureRegion;
        this.terrain = terrain;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void setId(int id) {
    }

    @Override
    public BlendMode getBlendMode() {
        return null;
    }

    @Override
    public void setBlendMode(BlendMode blendMode) {
    }

    @Override
    public TextureRegion getTextureRegion() {
        return this.textureRegion;
    }

    @Override
    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    @Override
    public float getOffsetX() {
        return 0;
    }

    @Override
    public void setOffsetX(float offsetX) {
    }

    @Override
    public float getOffsetY() {
        return 0;
    }

    @Override
    public void setOffsetY(float offsetY) {
    }

    @Override
    public MapProperties getProperties() {
        return null;
    }

    @Override
    public MapObjects getObjects() {
        return null;
    }

    public TerrainDefinition getTerrain() {
        return terrain;
    }

    public double getTerrainValue() {
        double value = (terrain.getMax() + terrain.getMin()) / 2;
        return value;
    }

    public void setTerrain(TerrainDefinition terrain) {
        this.terrain = terrain;
    }
}
