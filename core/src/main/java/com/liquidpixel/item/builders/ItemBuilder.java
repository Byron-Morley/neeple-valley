package com.liquidpixel.item.builders;

import com.liquidpixel.core.components.core.StatusComponent;
import com.liquidpixel.main.components.render.RenderComponent;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.sprite.components.AnimableSpriteComponent;
import com.liquidpixel.sprite.components.SpriteComponent;
import com.liquidpixel.sprite.components.StackedSpritesComponent;
import com.liquidpixel.core.core.Direction;
import com.liquidpixel.core.core.Action;
import com.liquidpixel.main.renderposition.ItemRenderPositionStrategy;
import com.liquidpixel.main.renderposition.RenderPositionStrategy;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.sprite.api.factory.ISpriteComponentFactory;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;
import com.liquidpixel.sprite.api.models.IAnimationDefinition;
import com.liquidpixel.sprite.api.models.IRamp;
import com.liquidpixel.main.model.RenderPriority;
import com.liquidpixel.sprite.components.RefreshSpriteRequirementComponent;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.liquidpixel.main.components.*;
import com.liquidpixel.main.components.colony.BuildingComponent;
import com.liquidpixel.main.components.colony.HouseComponent;
import com.liquidpixel.main.components.inits.BuildingDoorInitComponent;
import com.liquidpixel.main.components.inits.ClickBehaviorInitComponent;
import com.liquidpixel.main.components.items.*;
import com.liquidpixel.main.components.items.ResourceComponent;
import com.liquidpixel.main.components.load.CreateFoundationFencesComponent;
import com.liquidpixel.main.components.load.DontSaveComponent;
import com.liquidpixel.main.components.selection.SelectableEntityComponent;
import com.liquidpixel.main.components.sprite.*;
import com.liquidpixel.main.components.storage.*;
import com.liquidpixel.main.components.workshop.JobComponent;
import com.liquidpixel.main.components.workshop.WorkshopComponent;
import com.liquidpixel.main.interfaces.IRecipe;
import com.liquidpixel.main.model.item.*;




public class ItemBuilder {
    private Entity entity;
    private ISpriteComponentFactory animationComponentFactory;
    ISpriteFactory spriteFactory;

    public ItemBuilder(String name, int quantity, ISpriteComponentFactory animationComponentFactory, ISpriteFactory spriteFactory) {
        this.entity = new Entity()
            .add(new ItemComponent(name, quantity));
        this.animationComponentFactory = animationComponentFactory;
        this.spriteFactory = spriteFactory;
    }

    public ItemBuilder isLayer(Item item) {
        return isLayer(item, null);
    }

    public ItemBuilder isLayer(Item item, IRamp ramp) {
        if (item.getSlot() != null) {
            entity.add(
                new SpriteComponent.Builder(item.getSpriteName()).ramp(ramp).order(item.getSlot().getRenderPriority()).build()
            );
        } else {
            System.out.println("Layer does not have Equip Slot");
        }
        return this;
    }


    public ItemBuilder withRender(String spriteName, Color color) {
        try {
            this.entity
                .add(new RenderComponent(new ItemRenderPositionStrategy(), RenderPriority.ITEM, color))
                .add(animationComponentFactory.createSpriteStackBuilderComponent())
                .add(animationComponentFactory.createRefreshSpriteStackBuilderComponent())
                .add(new SpriteComponent.Builder(spriteName).build());

        } catch (Exception e) {
            Gdx.app.error("ItemBuilder", spriteName);
            Gdx.app.error("ItemBuilder", "Error: " + e.getMessage());
        }
        return this;
    }

    public ItemBuilder withAnimations(RenderPositionStrategy renderPositionStrategy, IAnimationDefinition animationDefinition, String spriteName, RenderPriority renderPriority) {
        entity.add(new RenderComponent(spriteFactory.getSprite(spriteName), renderPositionStrategy, renderPriority))
            .add(new StatusComponent(Action.IDLE, Direction.NONE))
            .add(new SpriteComponent.Builder(spriteName).build())
            .add(animationComponentFactory.createSpriteStackBuilderComponent())
            .add(animationComponentFactory.createRefreshSpriteStackBuilderComponent())
            .add(new AnimableSpriteComponent())
            .add(new StackedSpritesComponent(animationDefinition))
            .add(new RefreshSpriteRequirementComponent()
            );
        return this;
    }

    public ItemBuilder withRender(String spriteName) {
        return withRender(spriteName, Color.WHITE);
    }

    public ItemBuilder withGhostRender(String spriteName) {
        return withRender(spriteName, new Color(1f, 1f, 1f, 0.5f));
    }


    public ItemBuilder withInvisibleRender(String spriteName) {
        return withRender(spriteName, new Color(1f, 1f, 1f, 0.0f));
    }

    public ItemBuilder pickupable() {
        entity.add(new PickupableComponent());
        return this;
    }

    public ItemBuilder withJobs(Item model) {
        entity.add(new JobComponent(model.getJobs()));
        return this;
    }


    public ItemBuilder equipable(String spritesheet, EquipmentInformation eq) {
        entity.add(new EquipableComponent(eq));
//            .add(new StackableSpriteComponent(spritesheet));
        return this;
    }

    public ItemBuilder add(Component component) {
        entity.add(component);
        return this;
    }

    public ItemBuilder withInformation(Item item) {
        entity.add(new InformationComponent(item.getLabel()));
        entity.add(new SelectableEntityComponent());
        return this;
    }

    public ItemBuilder withTileset(String region) {
        entity.add(new TileableComponent(region));
        return this;
    }

    public ItemBuilder withTileset(String sync, String region, IAnimationDefinition animationDefinition, String spriteName, RenderPriority renderPriority) {
        entity.add(new TileableComponent(region))
            .add(new SpriteComponent(spriteName))
            .add(new RenderComponent(new ItemRenderPositionStrategy(), renderPriority, Color.WHITE))
            .add(new StatusComponent(Action.IDLE, Direction.NONE))
            .add(new AnimableSpriteComponent())
            .add(new StackedSpritesComponent(animationDefinition))
            .add(new RefreshSpriteRequirementComponent())
            .add(new SpriteComponent.Builder(spriteName).build())
            .add(animationComponentFactory.createSpriteStackBuilderComponent())
            .add(animationComponentFactory.createRefreshSpriteStackBuilderComponent())
        ;

        return this;
    }

    public Entity getItem() {
        return entity;
    }

    public Entity build() {
        return entity;
    }

    public ItemBuilder isHarvestable(String recipe, String tool) {
        entity.add(new HarvestableComponent(recipe, tool));
        return this;
    }

    public ItemBuilder withBody(String body) {

        BodyComponent bodyComponent = new BodyComponent(body, false);
        entity.add(bodyComponent);

        if (bodyComponent.hasCollision()) {
            entity.add(new CollisionComponent());
        }

        return this;
    }

    public ItemBuilder withFoundationBody(String body) {

        BodyComponent bodyComponent = new BodyComponent(body, true);
        entity.add(bodyComponent);

        if (bodyComponent.hasCollision()) {
            entity.add(new CollisionComponent());
        }

        return this;
    }


    public ItemBuilder withFoundation() {
        entity.add(new FoundationComponent());
        entity.add(new CreateFoundationFencesComponent());
        return this;
    }

    public ItemBuilder withColonyResource() {
        entity.add(new ColonyComponent());
        return this;
    }

    public ItemBuilder withoutSave() {
        entity.add(new DontSaveComponent());
        return this;
    }

    public ItemBuilder withResource(float harvestTime) {
        entity.add(new ResourceComponent(harvestTime));
        return this;
    }

    public ItemBuilder withClickBehavior(String behaviorId) {
        entity.add(new ClickBehaviorInitComponent(behaviorId));
        return this;
    }

    public ItemBuilder withHouse(House house) {
        entity.add(new HouseComponent(house.getCapacity()));
        return this;
    }

    public ItemBuilder isProvider() {
        entity.add(new ProviderComponent());
        return this;
    }

    public ItemBuilder isIcon() {
        entity.add(new IconComponent());
        return this;
    }

    public ItemBuilder isUI() {
        entity.add(new UIComponent());
        return this;
    }

    public void withStorage(Storage storage) {
        entity.add(new StorageComponent(storage));
        if (storage.isWorkshop()) entity.add(new WorkshopComponent());
        if (storage.isTile()) entity.add(new StorageTileComponent());
        if (storage.isGroup()) entity.add(new StorageGroupComponent());
    }

    public void withCostModifier(float costModifier) {
        entity.add(new PathComponent(costModifier));
    }

    public void isConsumer(IRecipe recipe) {
        entity.add(new ConsumerComponent(recipe));
        entity.add(new CreateConsumerWorkComponent());
    }

    public void withSpawnConfiguration(String spawnConfig) {
        //TODO improve later if we add more
        if (spawnConfig.equals("area")) {
            entity.add(new AreaSpawnConfigurationComponent());
        }
    }

    public void withGridInformation(GridInformation gridInformation) {
        entity.add(new TileSelectionComponent(gridInformation));
    }

    public void updateStorageRender() {
        entity.add(new StorageRenderRefreshComponent());
    }

    public void withDoor() {
        entity.add(new DoorComponent());
        if (Mappers.status.has(entity)) {
            StatusComponent statusComponent = Mappers.status.get(entity);
            statusComponent.setAction(Action.CLOSED);
        } else {
            System.out.println("Door not setup correctly");
        }
    }

    public void withBuilding(Building building) {
        entity.add(new BuildingComponent());
        entity.add(new BuildingInitComponent());
        if (building.hasDoor()) {
            entity.add(new BuildingDoorInitComponent());
        }
    }

    public void withFarm(Farm farm) {
        entity.add(new FarmComponent(farm.getOrigin(), farm.getPlots()));
    }

    public void withGrowable(Growable growable) {
        entity.add(new GrowableComponent(growable));
    }
}
