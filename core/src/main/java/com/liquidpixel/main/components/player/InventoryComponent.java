package com.liquidpixel.main.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import static com.liquidpixel.main.utils.events.Messages.INVENTORY_CHANGED;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryComponent implements Component {

    @JsonProperty
    private ObservableList<Entity> inventory;

    @JsonProperty
    private int inventoryLimit;

    @JsonCreator
    public InventoryComponent(ObservableList<Entity> inventory, int inventoryLimit) {
        this.inventory = inventory;
        this.inventoryLimit = inventoryLimit;
        inventory.addListener((ListChangeListener<? super Entity>) c -> {
            MessageManager.getInstance().dispatchMessage(INVENTORY_CHANGED);
        });
    }

    public InventoryComponent(int inventoryLimit) {
        this.inventoryLimit = inventoryLimit;
        initInventory();
    }

    private void initInventory() {
        inventory = FXCollections.observableArrayList();
        inventory.addListener((ListChangeListener<? super Entity>) c -> {
            MessageManager.getInstance().dispatchMessage(INVENTORY_CHANGED);
        });
    }

    public int getInventoryLimit() {
        return inventoryLimit;
    }

    @JsonIgnore
    public ObservableList<Entity> getInventory() {
        return inventory;
    }

    public void setInventory(ObservableList<Entity> inventory) {
        this.inventory = inventory;
    }

    public void addItem(Entity item) {
        if (inventory.size() < inventoryLimit) {
            inventory.add(item);
        } else {
            System.out.println("Inventory is full");
        }
    }
}
