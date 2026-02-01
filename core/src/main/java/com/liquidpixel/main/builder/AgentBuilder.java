package com.liquidpixel.main.builder;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.core.components.core.StatusComponent;
import com.liquidpixel.main.components.*;
import com.liquidpixel.main.components.agent.AgentComponent;
import com.liquidpixel.main.components.ai.BehaviorComponent;
import com.liquidpixel.main.components.camera.CameraComponent;
import com.liquidpixel.main.components.camera.CameraFocusComponent;
import com.liquidpixel.main.components.items.EquipmentComponent;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.components.player.*;
import com.liquidpixel.main.components.render.RenderComponent;
import com.liquidpixel.main.components.selection.SelectableEntityComponent;
import com.liquidpixel.main.renderposition.SpriteRenderPositionStrategy;
import com.liquidpixel.sprite.components.AnimableSpriteComponent;
import com.liquidpixel.sprite.api.models.IAnimationDefinition;
import com.liquidpixel.sprite.components.StackedSpritesComponent;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.sprite.model.Layer;
import com.liquidpixel.main.model.RenderPriority;
import com.liquidpixel.main.model.equip.EquipSlot;
import com.liquidpixel.main.model.person.Person;

import static com.liquidpixel.main.utils.PlayerUtils.INVENTORY_LIMIT;

import java.util.List;
import java.util.Map;

import com.liquidpixel.main.components.agent.WorkerComponent;
import com.liquidpixel.sprite.api.factory.ISpriteComponentFactory;
import com.liquidpixel.sprite.components.LayerBuildComponent;
import com.liquidpixel.sprite.components.RefreshLayerBuildComponent;
import com.liquidpixel.sprite.components.RefreshSpriteRequirementComponent;

public class AgentBuilder {
    private Entity entity;
    private ISpriteComponentFactory spriteComponentFactory;

    public AgentBuilder(String agentId, ISpriteComponentFactory spriteComponentFactory) {
        entity = new Entity()
            .add(new AgentComponent(agentId))
            .add(new StatusComponent());
        this.spriteComponentFactory = spriteComponentFactory;
    }

    public AgentBuilder withVelocity(float velocity) {
        entity.add(new VelocityComponent(velocity));
        return this;
    }

    public AgentBuilder at(int x, int y) {
        entity.add(new PositionComponent(x, y));
        return this;
    }

    public AgentBuilder withAnimations(IAnimationDefinition animationDefinition, String id) {

        RenderComponent renderComponent = new RenderComponent(new SpriteRenderPositionStrategy(), RenderPriority.AGENT, true);
        //TODO hardcoded in maybe cause an issue later
        renderComponent.setWidth(1);
        renderComponent.setHeight(2);

        entity.add(renderComponent)
            .add(spriteComponentFactory.createSpriteStackBuilderComponent())
            .add(spriteComponentFactory.createRefreshSpriteStackBuilderComponent())
            .add(new StackedSpritesComponent(animationDefinition))
            .add(new AnimableSpriteComponent())
            .add(new RefreshSpriteRequirementComponent());
        return this;
    }

    public AgentBuilder withEquipment(Map<EquipSlot, Entity> equipment, ISpriteComponentFactory spriteComponentFactory) {
//        entity.add(spriteComponentFactory.createWearComponent(equipment));
        return this;
    }

    public AgentBuilder isCamera() {
        entity.add(new CameraComponent());
        return this;
    }

    public AgentBuilder withInventory() {
        entity.add(new InventoryComponent(INVENTORY_LIMIT));
        return this;
    }

    public void withPlayerControls() {
        entity.add(new PlayerControlComponent());
        entity.add(new CameraFocusComponent());
    }

    public void withCarrySlot() {
        entity.add(new CarryComponent());
    }

    public Entity build() {
        return entity;
    }

    public void withAiBehavior(String behaviorFileName) {
        entity.add(new BehaviorComponent(behaviorFileName));
    }

    public void makeSelectable() {
        entity.add(new SelectableEntityComponent());
    }

    public void withWorker(Person person) {
        entity.add(new WorkerComponent(person));
    }

    public void withSettlement(String settlement) {
        entity.add(new SettlementComponent(settlement, entity));

    }

    public void withStorage(int slots) {
        entity.add(new StorageComponent(slots));
    }

    public void withEquipment() {
        entity.add(new EquipmentComponent());
    }

    public AgentBuilder withBody(String body) {

        BodyComponent bodyComponent = new BodyComponent(body, false);
        entity.add(bodyComponent);

        if (bodyComponent.hasCollision()) {
            entity.add(new CollisionComponent());
        }

        return this;
    }

    public void withLayers(List<Layer> layers) {
        entity.add(new LayerBuildComponent(layers));
        entity.add(new RefreshLayerBuildComponent());
    }
}
