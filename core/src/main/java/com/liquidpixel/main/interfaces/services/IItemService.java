package com.liquidpixel.main.interfaces.services;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.item.builders.ItemBuilder;
import com.liquidpixel.main.interfaces.*;
import com.liquidpixel.main.interfaces.work.IWorkService;
import com.liquidpixel.main.model.item.Item;
import com.liquidpixel.sprite.api.component.ISpriteComponent;
import com.liquidpixel.sprite.api.models.IRamp;

import java.util.List;
import java.util.Map;

public interface IItemService {

    IWorkService getWorkService();

    void spawnItem(Entity entity, GridPoint2 position);

    void spawnItem(Entity entity);

    void spawnAndPickupItem(Entity entity);

    void setupTileset(Entity entity, GridPoint2 position);

    void dispose(Entity entity);

    List<Entity> getPickuableItems();

    List<Entity> getBuildableItems();

    ItemBuilder getItem(String name, int quantity);

    ItemBuilder getItem(String name);

    ItemBuilder getLayer(String name);

    ItemBuilder getLayer(String name, IRamp ramp);

    ItemBuilder getFoundationItem(String name);

    ItemBuilder getGhostBuilding(String name);

    IRecipe getRecipe(String name);

    ItemBuilder getGhostItem(String name, int quantity);

    Map<String, Item> getModels();

    List<IStorageItem> getColonyComponents();

    List<Entity> getTerrainItemsInArea(GridPoint2 start, GridPoint2 end);

    ISpriteComponent getSpriteComponent(String name, int mod);

    IStorageItem getStorageItem(Entity entity);
}
