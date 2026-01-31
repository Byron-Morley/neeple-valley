package com.liquidpixel.main.managers;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.components.*;
import com.liquidpixel.main.components.load.CreateFoundationFencesComponent;
import com.liquidpixel.main.components.load.DontSaveComponent;
import com.liquidpixel.main.components.player.InventoryComponent;
import com.liquidpixel.main.components.sprite.*;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.engine.GameState;
import com.liquidpixel.main.interfaces.*;
import com.liquidpixel.main.interfaces.managers.ILoadAndSaveManager;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.interfaces.ui.IWindowService;
import com.liquidpixel.main.listeners.WorldMapReadyListener;
import com.liquidpixel.main.model.ai.Job;
import com.liquidpixel.main.model.item.Recipe;
import com.liquidpixel.main.model.sprite.NodeType;
import com.liquidpixel.main.serialisers.*;
import com.liquidpixel.main.serialisers.Entity.EntityDeserializer;
import com.liquidpixel.main.serialisers.Entity.EntitySerializer;
import com.liquidpixel.main.serialisers.Recipe.RecipeDeserializer;
import com.liquidpixel.main.serialisers.Recipe.RecipeSerializer;
import com.liquidpixel.main.utils.Mappers;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.liquidpixel.selection.api.IClickBehaviorService;
import com.sun.javafx.collections.ObservableMapWrapper;

import java.io.IOException;
import java.util.*;

public class LoadAndSaveManager implements ILoadAndSaveManager {

    IMapService mapService;
    IItemService itemService;
    IWindowService windowService;
    ISelectionService selectionService;
    IWorldMap worldMap;
    IClickBehaviorService clickBehaviorService;

    private Engine engine;
    private ObjectMapper mapper;
    List<Class<? extends Component>> excludeList = Arrays.asList(
        InventoryComponent.class
    );

    public void init(
        IMapService mapService,
        IItemService itemService,
        IWindowService windowService,
        ISelectionService selectionService,
        IClickBehaviorService clickBehaviorService
    ) {
        this.mapService = mapService;
        this.itemService = itemService;
        this.windowService = windowService;
        this.selectionService = selectionService;
        this.clickBehaviorService = clickBehaviorService;

        this.worldMap = mapService.getWorldMap();
        this.engine = GameResources.get().getEngine();
        this.mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enableDefaultTyping();
        mapper.registerModule(new SimpleModule()
            .addSerializer(Entity.class, new EntitySerializer())
            .addDeserializer(Entity.class, new EntityDeserializer())
            .addSerializer(Vector2.class, new Vector2Serializer())
            .addDeserializer(Vector2.class, new Vector2Deserializer())
            .addSerializer(GridPoint2.class, new GridPoint2Serializer())
            .addDeserializer(GridPoint2.class, new GridPoint2Deserializer())
            .addDeserializer(NodeType.class, new NodeTypeDeserializer())
            .addDeserializer(Job.class, new JobDeserializer())
            .addSerializer(Recipe.class, new RecipeSerializer())
            .addDeserializer(Recipe.class, new RecipeDeserializer())
//            .addDeserializer(ObservableMapWrapper.class, new ObservableMapWrapperDeserializer())
            .addKeyDeserializer(GridPoint2.class, new GridPoint2KeyDeserializer())
            .addDeserializer(ClickableComponent.class, new ClickableComponentDeserializer(windowService, clickBehaviorService))
        );
    }

    public void saveGame(String saveName, Runnable onComplete) {
        SaveData saveData = new SaveData();

        saveData.terrainLedger = mapService.getWorldMap().getTerrainLedger();
        saveData.entities = new ArrayList<>();
        saveData.entityIds = new ArrayList<>();
        saveData.seed = GameState.getSeed();
        saveData.selectedSettlement = selectionService.getSelectedSettlement();

        ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.exclude(DontSaveComponent.class).get());

        for (Entity entity : entities) {
            EntityData entityData = new EntityData();

            for (Component component : entity.getComponents()) {
                if (component instanceof EntityComponent) {
                    saveData.entityIds.add(((EntityComponent) component).getId());
                    break;
                }
            }

            for (Component component : entity.getComponents()) {
                if (!excludeList.contains(component.getClass())) {
                    String componentName = component.getClass().getSimpleName();
                    String fieldName = Character.toLowerCase(componentName.charAt(0)) + componentName.substring(1);
                    entityData.components.put(fieldName, component);
                }
            }

            saveData.entities.add(entityData);
        }

        FileHandle file = Gdx.files.local("saves/" + saveName + ".yaml");
        try {
            file.writeString(mapper.writeValueAsString(saveData), false);
            onComplete.run();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    public void loadGame(String saveName, Runnable onComplete) {

        FileHandle file = Gdx.files.local("saves/" + saveName + ".yaml");
        SaveData saveData = getSaveData(file);

        worldMap.setTerrainLedger(saveData.terrainLedger);
        worldMap.addReadyListener(new WorldMapReadyListener() {
            @Override
            public void onWorldMapReady() {
                worldMap.reinit();
                loadEngine(file, onComplete);
            }
        });
        GameState.setSeed(saveData.seed);
        engine.removeAllEntities();
        mapService.reset();
    }


    private void loadEngine(FileHandle file, Runnable onComplete) {
        SaveData saveData = getSaveData(file);
        ArrayList<Entity> entityList = new ArrayList<>();

        // Create entities using the stored IDs
        for (String id : saveData.entityIds) {
            Entity entity = engine.createEntity();
            entity.add(new EntityComponent(id));
            entityList.add(entity);
            engine.addEntity(entity);
        }

        // Load data twice to aid in serialization for Entities
        saveData = getSaveData(file);
        for (int i = 0; i < entityList.size(); i++) {

            EntityData entityData = saveData.entities.get(i);
            Entity entity = entityList.get(i);

            for (Map.Entry<String, Component> entry : entityData.components.entrySet()) {
                if (!entry.getKey().equals("entityComponent")) {
                    entity.add(entry.getValue());
                }
            }

//            entity.add(new RefreshSpriteRequirementComponent());

            if (Mappers.foundation.has(entity)) {
                entity.add(new CreateFoundationFencesComponent());
            }
        }

        if (saveData.selectedSettlement != null) {
            String selectedId = ((EntityComponent) saveData.selectedSettlement.getComponent(EntityComponent.class)).getId();
            for (Entity entity : entityList) {
                EntityComponent ec = Mappers.entity.get(entity);
                if (ec.getId().equals(selectedId)) {
                    selectionService.setSelectedSettlement(entity);
                    break;
                }
            }
        }

        onComplete.run();
    }

    private SaveData getSaveData(FileHandle file) {
        try {
            SaveData saveData = mapper.readValue(file.readString(), SaveData.class);
            return saveData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void newGame(Runnable onComplete) {
        engine.removeAllEntities();
        Random random = new Random();
        int seed = random.nextInt();
        GameState.setSeed(seed);
        mapService.reset();
        onComplete.run();
    }
}


class SaveData {
    public Map<GridPoint2, String> terrainLedger;
    public List<String> entityIds;
    public List<EntityData> entities;
    public long seed;
    public Entity selectedSettlement;
}

@JsonInclude(JsonInclude.Include.NON_NULL)
class EntityData {

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    public Map<String, Component> components = new HashMap<>();
}
