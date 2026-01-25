package com.liquidpixel.item.factories;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.item.builders.ItemBuilder;
import com.liquidpixel.main.components.*;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.factories.ModelFactory;
import com.liquidpixel.main.interfaces.IRecipe;
import com.liquidpixel.main.model.item.Item;
import com.liquidpixel.main.model.item.Storage;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.main.model.sprite.BodyOffset;
import com.liquidpixel.main.renderposition.EquipmentRenderPositionStrategy;
import com.liquidpixel.main.renderposition.ItemRenderPositionStrategy;
import com.liquidpixel.main.renderposition.RenderPositionStrategy;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.sprite.api.factory.IAnimationFactory;
import com.liquidpixel.sprite.api.factory.ISpriteComponentFactory;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;
import com.liquidpixel.sprite.api.models.IAnimationDefinition;
import com.liquidpixel.sprite.api.models.IRamp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ItemFactory {

    ISpriteComponentFactory spriteComponentFactory;
    IAnimationFactory animationFactory;
    ISpriteFactory spriteFactory;

    static Map<String, Item> models;
    static Map<String, IRecipe> recipes;
    static Map<String, BodyOffset> bodies;


    int itemCount = 0;

    public ItemFactory(
        ISpriteComponentFactory spriteComponentFactory,
        IAnimationFactory animationFactory,
        ISpriteFactory spriteFactory
    ) {
        models = ModelFactory.getItemsModel();
        recipes = ModelFactory.getRecipesModel(models, spriteFactory);
        bodies = ModelFactory.getBodyOffsetModel();

        this.spriteComponentFactory = spriteComponentFactory;
        this.animationFactory = animationFactory;
        this.spriteFactory = spriteFactory;

        updateAvailableItemsFile();
    }

    public ItemBuilder getItem(String name) {
        return this.getItem(name, 1);
    }

    public ItemBuilder getItem(String name, int quantity) {
        String id = setId(name);
        try {
            Item model = models.get(name);

            if (model == null) {
                System.out.println("Item model is null for name: " + name);
                return null;
            }

            if (model.isResource()) {
                Item invisibleStorage = models.get("storage/invisible_storage");
                ItemComponent itemComponent = new ItemComponent(name, quantity);
                ItemBuilder itemBuilder = buildItem(invisibleStorage, 1);
                Entity entity = itemBuilder.build();
                StorageComponent storageComponent = Mappers.storage.get(entity);
                storageComponent.addItem(new StorageItem(itemComponent.getName(), itemComponent.getQuantity(), itemComponent.getStackSize(), spriteFactory.getSprite(model.getSprite())));
                storageComponent.setOneUse(true);
                itemBuilder.updateStorageRender();

                return itemBuilder;
            } else {

                return buildItem(model, quantity);
            }
        } catch (Exception e) {
            System.out.println("Error creating item for name: " + name);
            System.out.println("Error details: " + e.getMessage());
            return null;
        }
    }

    private ItemBuilder buildItem(Item model, int quantity) {
        ItemBuilder itemBuilder = new ItemBuilder(model.getName(), quantity, spriteComponentFactory, spriteFactory);

        try {
            if (model.getAnimation().isEnabled()) {

                try {
                    IAnimationDefinition animationDefinition = animationFactory.get(model.getAnimation().getModel());

                    try {
                        if (model.getTileset() != null) {
                            itemBuilder.withTileset(
                                model.getAnimation().getSync(),
                                model.getTileset().getRegion(),
                                animationDefinition,
                                model.getSpriteName(),
                                model.getRenderPriority());
                        } else {
                            if (model.isRender()) {
                                RenderPositionStrategy renderPositionStrategy = new ItemRenderPositionStrategy();
                                if (model.isEquipable()) renderPositionStrategy = new EquipmentRenderPositionStrategy();
                                itemBuilder.withAnimations(renderPositionStrategy, animationDefinition, model.getSpriteName(), model.getRenderPriority());
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error in tileset/render processing for " + model.getName() + ": " + e.getMessage());
                        throw e;
                    }
                } catch (Exception e) {
                    System.err.println("Error loading animation model '" + model.getAnimation().getModel() + "' for item " + model.getName() + ": " + e.getMessage());
                    throw e;
                }
            } else {
                System.out.println("Animations disabled for item: " + model.getName());

                try {
                    if (model.getTileset() != null) {
                        itemBuilder.withTileset(model.getTileset().getRegion());
                        itemBuilder.withRender(model.getSprite());
                    }
                } catch (Exception e) {
                    System.err.println("Error in tileset processing (no animations) for " + model.getName() + ": " + e.getMessage());
                    throw e;
                }
            }
        } catch (Exception e) {
            System.err.println("Error in animation/render setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }

        try {
            if (model.isEquipable()) {
                //TODO fix this

//                EquipmentInformation eq = model.getEquipmentInformation();
//                itemBuilder.equipable(model.getSpriteName(), eq);
            }
        } catch (Exception e) {
            System.err.println("Error in equipable setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }

        try {
            if (model.hasInformationComponent()) {
                itemBuilder.withInformation(model);
            }
        } catch (Exception e) {
            System.err.println("Error in information component setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }

        try {
            if (model.getHouse() != null) {
                itemBuilder.withHouse(model.getHouse());
            }
        } catch (Exception e) {
            System.err.println("Error in house setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }


        try {
            if (model.isProvider()) {
                itemBuilder.isProvider();
            }
        } catch (Exception e) {
            System.err.println("Error in provider setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }

        try {
            if (model.hasStorage()) {
                itemBuilder.withStorage(model.getStorage());
            }
        } catch (Exception e) {
            System.err.println("Error in storage setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }

        try {
            if (model.isHarvestable()) {
                itemBuilder.isHarvestable(model.getHarvestInformation().getRecipe(), model.getHarvestInformation().getTool());
            }
        } catch (Exception e) {
            System.err.println("Error in harvestable setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }

        try {
            if (model.isPickupable()) {
                itemBuilder.pickupable();
            }
        } catch (Exception e) {
            System.err.println("Error in pickupable setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }

        try {
            if (model.getJobs() > 0) {
                itemBuilder.withJobs(model);
            }
        } catch (Exception e) {
            System.err.println("Error in jobs setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }

        try {
            if (model.isFoundation()) {
                itemBuilder.withFoundation();
            }
        } catch (Exception e) {
            System.err.println("Error in foundation setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }

        try {
            if (model.hasBody()) {
                itemBuilder.withBody(model.getBodyModel());
            }
        } catch (Exception e) {
            System.err.println("Error in body setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }

        try {
            if (model.isColonyResource()) {
                itemBuilder.withColonyResource();
            }
        } catch (Exception e) {
            System.err.println("Error in colony resource setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }

        try {
            if (model.isDontSave()) {
                itemBuilder.withoutSave();
            }
        } catch (Exception e) {
            System.err.println("Error in dont save setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }

        try {
            if (model.getResourceInformation() != null) {
                itemBuilder.withResource(model.getHarvestTime());
            }
        } catch (Exception e) {
            System.err.println("Error in resource information setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }

        try {
            if (model.getGridInformation() != null) {
                itemBuilder.withGridInformation(model.getGridInformation());
            }
        } catch (Exception e) {
            System.err.println("Error in grid information setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }

        try {
            if (model.getSpawnConfig() != null) {
                itemBuilder.withSpawnConfiguration(model.getSpawnConfig());
            }
        } catch (Exception e) {
            System.err.println("Error in spawn configuration setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }

        try {
            if (model.getClickBehavior() != null) {
                String behaviorId = model.getClickBehavior();
                itemBuilder.withClickBehavior(behaviorId);
            }
        } catch (Exception e) {
            System.err.println("Error in click behavior setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }

        try {
            if (model.getCostModifier() != 0) {
                itemBuilder.withCostModifier(model.getCostModifier());
            }
        } catch (Exception e) {
            System.err.println("Error in cost modifier setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }

        try {
            if (model.isDoor()) {
                itemBuilder.withDoor();
            }
        } catch (Exception e) {
            System.err.println("Error in door setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }


        try {
            if (model.getFarm() != null) {
                itemBuilder.withFarm(model.getFarm());
            }
        } catch (Exception e) {
            System.err.println("Error in farm setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }


        try {
            if (model.getGrowable() != null) {
                itemBuilder.withGrowable(model.getGrowable());
            }
        } catch (Exception e) {
            System.err.println("Error in growable setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }


        try {
            if (model.getBuilding() != null) {
                itemBuilder.withBuilding(model.getBuilding());
            }
        } catch (Exception e) {
            System.err.println("Error in door setup for " + model.getName() + ": " + e.getMessage());
            throw e;
        }


        return itemBuilder;
    }

    public ItemBuilder getFoundationItem(String name) {
        String id = setId(name);
        Item model = models.get(name);

        ItemBuilder itemBuilder = new ItemBuilder(name, 1, spriteComponentFactory, spriteFactory);
        itemBuilder.withInvisibleRender(model.getSpriteName());
        itemBuilder.withFoundation();

        if (model.hasBody()) {
            itemBuilder.withFoundationBody(model.getBodyModel());
        }

        String recipe = name.substring(name.lastIndexOf('/') + 1);
        itemBuilder.isConsumer(getRecipe(recipe));
        itemBuilder.withStorage(new Storage(8));

        //TODO: Fix
        itemBuilder.withClickBehavior("storage_window");

        return itemBuilder;
    }

    public ItemBuilder getGhostBuilding(String name) {
        String id = setId(name);
        Item model = models.get(name);

        ItemBuilder itemBuilder = new ItemBuilder(name, 1, spriteComponentFactory, spriteFactory);

        if (model.hasBody()) {
            itemBuilder.withFoundationBody(model.getBodyModel());
            itemBuilder.add(new TileSelectionComponent(true, true));
        } else {
            itemBuilder.add(new TileSelectionComponent(1, 1));
        }

        return itemBuilder;
    }

    public ItemBuilder getLayer(String name) {
        return getLayer(name, null);
    }

    public ItemBuilder getLayer(String name, IRamp ramp) {
        String id = setId(name);
        Item model = models.get(name);

        return new ItemBuilder(name, 1, spriteComponentFactory,spriteFactory).isLayer(model, ramp);
    }

    private String setId(String name) {
        return name + "_" + ++itemCount;
    }

    public ItemBuilder getGhostItem(String name, int quantity) {
        String id = setId(name);
        Item model = models.get(name);

        return new ItemBuilder(name, quantity, spriteComponentFactory,spriteFactory)
            .withGhostRender(model.getSpriteName());
    }

    private void updateAvailableItemsFile() {
        File file = new File("available_items.txt");
        int currentCount = models.size();

        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                if (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("Count: ")) {
                        try {
                            int savedCount = Integer.parseInt(line.substring(7).trim());
                            if (savedCount == currentCount) return;
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading available_items.txt: " + e.getMessage());
            }
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Count: " + currentCount + "\n");
            List<String> sortedKeys = new ArrayList<>(models.keySet());
            Collections.sort(sortedKeys);
            for (String key : sortedKeys) {
                writer.write(key + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing available_items.txt: " + e.getMessage());
        }
    }

    public static Map<String, Item> getModels() {
        return models;
    }

    public static IRecipe getRecipe(String name) {
        if (name.contains("/")) {
            String extractedName = name.substring(name.lastIndexOf('/') + 1);
            return recipes.get(extractedName);
        }

        return recipes.get(name);
    }


    public static BodyOffset getBodyOffset(String name) {
        return bodies.get(name);
    }

}
