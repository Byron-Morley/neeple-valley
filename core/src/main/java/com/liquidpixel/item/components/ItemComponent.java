package com.liquidpixel.item.components;

import com.badlogic.ashley.core.Component;
import com.liquidpixel.main.factories.ModelFactory;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.sprite.model.GameSprite;
import com.liquidpixel.main.model.item.Item;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.liquidpixel.main.utils.utils.getFilenameFromPath;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemComponent implements Component {

    @JsonProperty
    final String name;

    @JsonProperty
    int quantity;

    @JsonIgnore
    final String label;

    @JsonIgnore
    GameSprite sprite;

    @JsonIgnore
    boolean isStackable;

    @JsonIgnore
    int stackSize;

    @JsonIgnore
    int unitSize;

    @JsonCreator
    public ItemComponent(
        @JsonProperty("name") String name,
        @JsonProperty("quantity") int quantity
    ) {
        this.name = name;
        this.quantity = quantity;

        Item item = ModelFactory.getItemsModel().get(name);
        try {
            if (item == null) {
                throw new IllegalArgumentException("Item not found: " + name);
            }
        } catch (Exception e) {
            System.err.println("Error creating ItemComponent with name: " + name);
            e.printStackTrace();
        }

        this.label = item.getLabel();
        this.isStackable = item.isStackable();
        this.stackSize = item.getStackSize();
        this.unitSize = item.getUnitSize();
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public GameSprite getSprite() {
        return sprite;
    }

    @JsonIgnore
    public GameSprite getIcon() {
        return getSprite();
    }

    @JsonIgnore
    public int getStackSize() {
        return stackSize;
    }

    @JsonIgnore
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @JsonIgnore
    public String getLabel() {
        return label;
    }

    @JsonIgnore
    public boolean isStackable() {
        return isStackable;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public int getUnitSize() {
        return unitSize;
    }

    public String getRecipeName() {
        return getFilenameFromPath(name);
    }

    public IStorageItem getItem() {
        return new StorageItem(name, quantity, stackSize, sprite);
    }

    public IStorageItem getItem(int quantity) {
        return new StorageItem(name, quantity, stackSize, sprite);
    }

    public void setSprite(GameSprite sprite) {
        this.sprite = sprite;
    }
}
