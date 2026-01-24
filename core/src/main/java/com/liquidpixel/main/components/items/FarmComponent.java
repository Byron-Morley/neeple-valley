package com.liquidpixel.main.components.items;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.interfaces.IStorageItem;

import java.util.ArrayList;
import java.util.List;

public class FarmComponent implements Component {

    boolean isLoop = true;
    boolean isPlanted = false;
    List<Entity> crops;
    String currentCrop;
    List<Entity> cropsToPlant;

    GridPoint2 origin;
    List<GridPoint2> relativePlotPoints;

    public FarmComponent(GridPoint2 origin, List<GridPoint2> relativePlotPoints) {
        this.origin = origin;
        this.relativePlotPoints = relativePlotPoints;
        this.crops = new ArrayList<>();
        this.cropsToPlant = new ArrayList<>();
    }

    public List<Entity> getCrops() {
        return crops;
    }

    public void setCrops(List<Entity> crops) {
        this.crops = crops;
    }

    public String getCurrentCrop() {
        return currentCrop;
    }

    public void setCurrentCrop(String currentCrop) {
        this.currentCrop = currentCrop;
    }

    public List<Entity> getCropsToPlant() {
        return cropsToPlant;
    }

    public void setCropsToPlant(List<Entity> cropsToPlant) {
        this.cropsToPlant = cropsToPlant;
    }

    public GridPoint2 getOrigin() {
        return origin;
    }

    public List<GridPoint2> getRelativePlotPoints() {
        return relativePlotPoints;
    }

    public void addCrop(Entity crop) {
        crops.add(crop);
    }

    public void removeCrop(Entity crop) {
        crops.remove(crop);
    }

    public void addCropToPlant(Entity crop) {
        cropsToPlant.add(crop);
    }

    public void removeCropToPlant(Entity crop) {
        cropsToPlant.remove(crop);
    }

    public boolean isLoop() {
        return isLoop;
    }

    public void setLoop(boolean loop) {
        isLoop = loop;
    }

    public boolean isPlanted() {
        return isPlanted;
    }

    public void setPlanted(boolean planted) {
        isPlanted = planted;
    }

    public boolean isEmpty() {
        return crops.isEmpty();
    }
}
