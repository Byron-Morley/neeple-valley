package com.liquidpixel.main.model.building;

import java.util.List;

public class BuildingInformation {
    private int effort;
    private List<Material> materials;

    public int getEffort() {
        return effort;
    }

    public void setEffort(int effort) {
        this.effort = effort;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }
}
