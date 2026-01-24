package com.liquidpixel.main.model.item;

import com.liquidpixel.main.model.RenderPriority;
import com.liquidpixel.main.model.building.BuildingInformation;
import com.liquidpixel.main.model.equip.EquipSlot;
import com.liquidpixel.main.model.sprite.Tileset;

import java.util.ArrayList;
import java.util.List;

import static com.liquidpixel.main.utils.utils.getFilenameFromPath;

public class Item {
    String name;
    String label;
    String sprite;
    EquipmentInformation equipmentInformation;
    BuildingInformation buildingInformation;
    HarvestInformation harvestInformation;
    ResourceInformation resourceInformation;
    GridInformation gridInformation;
    Information information;
    Growable growable;
    Farm farm;
    House house;
    Storage storage;
    boolean pickupable;
    boolean collision;
    Tileset tileset;
    Animation animation = new Animation();
    Building building;
    String bodyModel;
    boolean colonyResource;
    boolean dontSave = false;
    int basePrice = 0;
    int unitSize = 1;
    int startingGold = 0;
    String clickBehavior;
    int jobs = 0;
    float costModifier;
    String spawnConfig;
    boolean door = false;
    boolean foundation;
    boolean render = true;
    boolean provider = true;
    RenderPriority renderPriority = RenderPriority.ITEM;
    EquipSlot slot;

    List<String> recipes = new ArrayList<>();

    public String getSprite() {
        return sprite;
    }

    public String getSpriteName() {
        return sprite;
    }

    public EquipmentInformation getEquipmentInformation() {
        return equipmentInformation;
    }

    public boolean isEquipable() {
        return this.equipmentInformation != null;
    }

    public boolean hasInformationComponent() {
        return this.information != null;
    }

    public BuildingInformation getBuildingInformation() {
        return buildingInformation;
    }

    public boolean hasStorage() {
        return this.storage != null;
    }

    public Storage getStorage() {
        return storage;
    }

    public boolean isPickupable() {
        return pickupable;
    }

    public boolean isCollision() {
        return collision;
    }

    public boolean isHarvestable() {
        return harvestInformation != null;
    }

    public Tileset getTileset() {
        return tileset;
    }

    public Information getInformation() {
        return information;
    }

    public boolean isStackable() {
        if (resourceInformation == null) {
            return false;
        } else {
            return resourceInformation.stackSize > 1;
        }
    }

    public int getStackSize() {
        if (resourceInformation == null) {
            return 1;
        } else {
            return resourceInformation.stackSize;
        }
    }

    public HarvestInformation getHarvestInformation() {
        return harvestInformation;
    }

    public List<String> getRecipes() {
        return recipes;
    }

    public String getLabel() {
        return label;
    }

    public boolean hasBody() {
        return bodyModel != null;
    }

    public boolean isFoundation() {
        return foundation;
    }

    public String getBodyModel() {
        return bodyModel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isColonyResource() {
        return colonyResource;
    }

    public void setColonyResource(boolean colonyResource) {
        this.colonyResource = colonyResource;
    }

    public boolean isDontSave() {
        return dontSave;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public int getUnitSize() {
        return unitSize;
    }

    public int getStartingGold() {
        return startingGold;
    }

    public String getClickBehavior() {
        return clickBehavior;
    }

    public Animation getAnimation() {
        return animation;
    }

    public House getHouse() {
        return house;
    }

    public int getJobs() {
        return jobs;
    }

    public float getCostModifier() {
        return costModifier;
    }

    public float getHarvestTime() {
        if (resourceInformation == null) {
            return 0;
        } else {
            return resourceInformation.getHarvestTime();
        }
    }

    public int getYield() {
        if (resourceInformation == null) {
            return 0;
        } else {
            return resourceInformation.getYield();
        }
    }

    public ResourceInformation getResourceInformation() {
        return resourceInformation;
    }

    public String getSpawnConfig() {
        return spawnConfig;
    }

    public String getRecipeName() {
        return getFilenameFromPath(name);
    }

    public GridInformation getGridInformation() {
        return gridInformation;
    }

    public boolean isRender() {
        return render;
    }

    public RenderPriority getRenderPriority() {
        return renderPriority;
    }

    public boolean isResource(){
        return resourceInformation != null;
    }

    public boolean isDoor() {
        return door;
    }

    public Building getBuilding() {
        return building;
    }

    public Farm getFarm() {
        return farm;
    }

    public Growable getGrowable() {
        return growable;
    }

    public void setGrowable(Growable growable) {
        this.growable = growable;
    }

    public boolean isProvider() {
        return provider;
    }

    public EquipSlot getSlot() {
        return slot;
    }
}
