package com.liquidpixel.item.services;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.agent.AgentComponent;
import com.liquidpixel.main.components.agent.WorkerComponent;
import com.liquidpixel.main.components.render.RenderComponent;
import com.liquidpixel.main.interfaces.managers.IItemManager;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.work.IWorkService;
import com.liquidpixel.sprite.model.GameSprite;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.item.builders.ItemBuilder;
import com.liquidpixel.main.components.*;
import com.liquidpixel.item.components.SpectralPickupComponent;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.main.components.items.PickupableComponent;
import com.liquidpixel.main.components.sprite.TileableComponent;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.generators.procedural.AutoTileUtils;
import com.liquidpixel.main.interfaces.*;
import com.liquidpixel.main.model.item.Item;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.main.services.Service;
import com.liquidpixel.main.services.WorkService;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.sprite.api.component.ISpriteComponent;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;
import com.liquidpixel.sprite.api.models.IRamp;
import com.liquidpixel.sprite.components.SpriteComponent;
import com.liquidpixel.sprite.model.Ramp;

import java.util.*;
import java.util.List;

import static com.liquidpixel.main.utils.utils.getList;

public class ItemService extends Service implements IItemService {
    IWorkService workService;
    IItemManager itemManager;
    ISpriteFactory  spriteFactory;
    ComponentMapper<RenderComponent> rm = Mappers.render;
    ComponentMapper<TileableComponent> tm = Mappers.tileset;

    public ItemService(IItemManager itemManager, ISpriteFactory  spriteFactory) {
        this.itemManager = itemManager;
        this.workService = new WorkService(this);
        this.spriteFactory = spriteFactory;
    }

    public void spawnItem(Entity entity, GridPoint2 position) {
        entity.add(new PositionComponent(position));
        getEngine().addEntity(entity);
    }

    public void spawnItem(Entity entity) {
//        entity.add(new PositionComponent(new Vector2(-200, -200)));
        getEngine().addEntity(entity);
    }

    public void spawnAndPickupItem(Entity entity) {
        entity.add(new SpectralPickupComponent());
        spawnItem(entity, new GridPoint2(-100, -100));
    }


    public void setupTileset(Entity entity, GridPoint2 position) {
        TileableComponent tileableComponent = tm.get(entity);
        if (tileableComponent != null) {
            ImmutableArray<Entity> tiles = getEngine().getEntitiesFor(Family.all(TileableComponent.class, PositionComponent.class).get());

            GridPoint2 topPosition = new GridPoint2(position.x, position.y + 1);
            GridPoint2 rightPosition = new GridPoint2(position.x + 1, position.y);
            GridPoint2 bottomPosition = new GridPoint2(position.x, position.y - 1);
            GridPoint2 leftPosition = new GridPoint2(position.x - 1, position.y);

            Entity top = getEntityAtPosition(tiles, topPosition);
            Entity right = getEntityAtPosition(tiles, rightPosition);
            Entity bottom = getEntityAtPosition(tiles, bottomPosition);
            Entity left = getEntityAtPosition(tiles, leftPosition);

            setupSingleTile(entity, position, tiles);
            if (top != null) setupSingleTile(top, topPosition, tiles);
            if (right != null) setupSingleTile(right, rightPosition, tiles);
            if (bottom != null) setupSingleTile(bottom, bottomPosition, tiles);
            if (left != null) setupSingleTile(left, leftPosition, tiles);
        }
    }

    public void setupSingleTile(Entity entity, GridPoint2 position, ImmutableArray<Entity> tiles) {
        TileableComponent tileableComponent = tm.get(entity);
        if (tileableComponent != null) {
            RenderComponent renderComponent = rm.get(entity);
            float scaleX = renderComponent.getSprites().get(0).getScaleX();
            float scaleY = renderComponent.getSprites().get(0).getScaleY();
            renderComponent.getSprites().clear();

            boolean top = doesTileExist(tiles, position.x, position.y + 1);
            boolean right = doesTileExist(tiles, position.x + 1, position.y);
            boolean bottom = doesTileExist(tiles, position.x, position.y - 1);
            boolean left = doesTileExist(tiles, position.x - 1, position.y);

            int index = AutoTileUtils.calculateTileIndex(top, right, bottom, left);
            GameSprite sprite = spriteFactory.getSprite(tileableComponent.getRegion(), index);
            sprite.setScale(scaleX, scaleY);
            renderComponent.add(sprite);
        }
    }

    private boolean doesTileExist(ImmutableArray<Entity> tiles, float x, float y, String region) {
        Entity entity = getEntityAtPosition(tiles, new GridPoint2((int) x, (int) y));
        if (entity != null) {
            TileableComponent tileableComponent = tm.get(entity);
            return tileableComponent.getRegion().equals(region);
        }
        return false;
    }

    private boolean doesTileExist(ImmutableArray<Entity> tiles, float x, float y) {
        return getEntityAtPosition(tiles, new GridPoint2((int) x, (int) y)) != null;
    }

    public Entity getEntityAtPosition(ImmutableArray<Entity> entities, GridPoint2 position) {
        for (Entity entity : entities) {
            PositionComponent positionComponent = Mappers.position.get(entity);
            if (positionComponent.getGridPosition().equals(position)) {
                return entity;
            }
        }
        return null;
    }

    public List<Entity> getWorkers() {
        return getList(getEngine().getEntitiesFor(Family.all(AgentComponent.class, WorkerComponent.class).get()));
    }

    public List<Entity> getBuildableItems() {
        return getList(getEngine().getEntitiesFor(Family.all(PositionComponent.class, ItemComponent.class, FoundationComponent.class).get()));
    }

    public List<Entity> getPickuableItems() {
        return getList(getEngine().getEntitiesFor(Family.all(PositionComponent.class, ItemComponent.class, PickupableComponent.class).get()));
    }

    @Override
    public ItemBuilder getItem(String name, int quantity) {
        return itemManager.getItemFactory().getItem(name, quantity);
    }

    @Override
    public ItemBuilder getItem(String name) {
        return itemManager.getItemFactory().getItem(name);
    }

    @Override
    public ItemBuilder getLayer(String name) {
        return itemManager.getItemFactory().getLayer(name);
    }

    @Override
    public ItemBuilder getLayer(String name, IRamp ramp) {
        return itemManager.getItemFactory().getLayer(name, ramp);
    }

    @Override
    public ItemBuilder getFoundationItem(String name) {
        return itemManager.getItemFactory().getFoundationItem(name);
    }

    @Override
    public ItemBuilder getGhostBuilding(String name) {
        return itemManager.getItemFactory().getGhostBuilding(name);
    }

    @Override
    public IRecipe getRecipe(String name) {
        return itemManager.getItemFactory().getRecipe(name);
    }

    @Override
    public ItemBuilder getGhostItem(String name, int quantity) {
        return itemManager.getItemFactory().getGhostItem(name, quantity);
    }

    @Override
    public Map<String, Item> getModels() {
        return itemManager.getItemFactory().getModels();
    }

    public void dispose(Entity entity) {
        if (entity != null) getEngine().removeEntity(entity);
    }

    public IWorkService getWorkService() {
        return workService;
    }

    public List<IStorageItem> getColonyComponents() {

        ImmutableArray<Entity> resources = GameResources.get().getEngine().getEntitiesFor(Family.all(ColonyComponent.class).get());

        List<IStorageItem> resourcesSet = new ArrayList<>();

        for (Entity resource : resources) {
            resourcesSet.add(getStorageItem(resource));
        }
        return resourcesSet;
    }

    public Entity splitItem(Entity entity, int quantity) {

        ItemComponent itemComponent = Mappers.item.get(entity);
        PositionComponent positionComponent = Mappers.position.get(entity);

        int newQuantity = itemComponent.getQuantity() - quantity;
        itemComponent.setQuantity(newQuantity);

        Entity splitItem = getItem(itemComponent.getName(), quantity).build();
        spawnItem(splitItem, positionComponent.getGridPosition());

        return splitItem;
    }

    //TODO: might be able to use the map graph instead of this
    @Override
    public List<Entity> getTerrainItemsInArea(GridPoint2 bottomLeft, GridPoint2 topRight) {
        List<Entity> result = new ArrayList<>();

        for (Entity entity : getEngine().getEntitiesFor(Family.all(
            PositionComponent.class,
            TerrainItemComponent.class).get())) {

            PositionComponent pos = Mappers.position.get(entity);
            if (pos.getX() >= bottomLeft.x && pos.getX() < topRight.x &&
                pos.getY() >= bottomLeft.y && pos.getY() < topRight.y) {
                result.add(entity);
            }
        }

        return result;
    }

    @Override
    public ISpriteComponent getSpriteComponent(String name, int mod) {
        String itemName = "clothes/" + name;
        Item item = getModels().get(itemName);
        IRamp ramp = new Ramp(item.getSlot().getRamp(), mod);
        return new SpriteComponent.Builder(item.getSpriteName())
            .ramp(ramp)
            .order(item.getSlot().getRenderPriority())
            .build();
    }

    public IStorageItem getStorageItem(Entity entity){
        ItemComponent itemComponent = entity.getComponent(ItemComponent.class);
        SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);
        GameSprite gameSprite = spriteFactory.getSprite(spriteComponent.getName());
        return new StorageItem(itemComponent.getName(), itemComponent.getQuantity(), itemComponent.getStackSize(), gameSprite);
    }

}
