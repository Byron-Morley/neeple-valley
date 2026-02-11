package com.liquidpixel.main.systems.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.agent.AgentComponent;
import com.liquidpixel.main.services.items.StorageHelper;
import com.liquidpixel.pathfinding.components.MovementTaskComponent;
import com.liquidpixel.main.components.ai.actions.HaulComponent;
import com.liquidpixel.main.components.player.CarryComponent;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.components.storage.StorageRenderRefreshComponent;
import com.liquidpixel.main.components.ui.MarkComponent;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.main.model.status.WorkState;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.sprite.components.RefreshSpriteRequirementComponent;

import static com.liquidpixel.main.utils.PositionUtils.reduceToCell;


public class HaulSystem extends IteratingSystem {
    IItemService itemService;
    IMapService mapService;
    IWorldMap worldMap;

    public HaulSystem(IMapService mapService, IItemService itemService) {
        super(Family.all(
            HaulComponent.class,
            AgentComponent.class,
            PositionComponent.class
        ).get());
        this.itemService = itemService;
        this.mapService = mapService;
        this.worldMap = mapService.getWorldMap();
    }

    @Override
    protected void processEntity(Entity agent, float delta) {
        HaulComponent hauling = agent.getComponent(HaulComponent.class);

        switch (hauling.state) {
            case IDLE:
                handleIdleState(agent, hauling);
                break;
            case MOVING_TO_ITEM:
                handleMovingToItemState(agent, hauling);
                break;
            case PICKING_UP:
                handlePickingUpState(agent, hauling);
                break;
            case MOVING_TO_DESTINATION:
                handleMovingToDestinationState(agent, hauling);
                break;
            case DROPPING_OFF:
                handleDroppingOffState(agent, hauling);
                break;
        }
    }

    private void handleMovingToItemState(Entity agent, HaulComponent hauling) {
        MovementTaskComponent movement = agent.getComponent(MovementTaskComponent.class);
        if (movement != null && movement.state == MovementTaskComponent.State.FINISHED) {
            hauling.state = WorkState.PICKING_UP;
        }
    }

    private void handleMovingToDestinationState(Entity agent, HaulComponent hauling) {
        MovementTaskComponent movement = agent.getComponent(MovementTaskComponent.class);
        if (movement != null && movement.state == MovementTaskComponent.State.FINISHED) {
            hauling.state = WorkState.DROPPING_OFF;
        }
    }

    private void handleDroppingOffState(Entity agent, HaulComponent hauling) {
        if (Mappers.carry.has(agent)) {
            getEngine().removeEntity(Mappers.carry.get(agent).getItem());
            agent.remove(CarryComponent.class);
        }

        StorageComponent agentStorage = Mappers.storage.get(agent);
        IStorageItem item = agentStorage.removeItem(hauling.getItemName());
        StorageComponent storage = Mappers.storage.get(hauling.getDestination());
        storage.unReserveSpace(item);

        if (Mappers.storageTile.has(hauling.getDestination())) hauling.getDestination().add(new StorageRenderRefreshComponent());

        try {
            StorageHelper.addItem(storage, item);
        } catch (Exception e) {
            agent.remove(CarryComponent.class);
            hauling.state = WorkState.FAILED;
            e.printStackTrace();
        }

        agent.remove(CarryComponent.class);
        hauling.state = WorkState.COMPLETED;
    }

    private void handlePickingUpState(Entity agent, HaulComponent hauling) {
        System.out.println("Picking up item");

        Entity storage = hauling.getOrigin();

        if (storage == null) {
            hauling.state = WorkState.FAILED;
            return;
        }

        int quantityToPickup = hauling.getItem().getQuantity();

        StorageComponent storageComponent = Mappers.storage.get(storage);
        IStorageItem storageItem = storageComponent.getItems().get(hauling.getItemName());

        //do we need to split the stack?
        if (quantityToPickup < storageItem.getQuantity()) {
            storageComponent.unReserveItem(hauling.getItem());
            StorageHelper.removeItem(storageComponent, hauling.getItem());
        } else {
            MarkComponent markComponent = Mappers.mark.get(storage);
            if (markComponent != null) getEngine().removeEntity(markComponent.getMark());
            storage.remove(MarkComponent.class);
            if (storageComponent.isOneUse()) getEngine().removeEntity(storage);
        }

        planMovingToDestination(agent, hauling);
    }

    private void planMovingToDestination(Entity agent, HaulComponent hauling) {
        IStorageItem item = hauling.getItem();
        StorageHelper.addItem(Mappers.storage.get(agent), item);

        Entity carryItem = itemService.getItem("storage/carry_storage").build();


        //TODO FIX ME

//        StackableSpriteComponent stackableSpriteComponent = Mappers.stackableSprite.get(carryItem);
//        stackableSpriteComponent.setItemSprite(item.getSpriteName());




        carryItem.add(new RefreshSpriteRequirementComponent());
        itemService.spawnItem(carryItem, new GridPoint2(-300, -300));
        agent.add(new CarryComponent(carryItem));

        GridPoint2 agentPosition = Mappers.position.get(agent).getGridPosition();
        GridPoint2 itemPosition = hauling.getItemDropLocation();
        GridPoint2 target = mapService.findValidInteractionPoint(agentPosition, itemPosition);

        if (target == null) {
            hauling.setInaccessiblePosition(itemPosition);
            hauling.state = WorkState.FAILED;
            return;
        }

        if (target.equals(agentPosition)) {
            hauling.state = WorkState.DROPPING_OFF;
        } else {
            hauling.state = WorkState.MOVING_TO_DESTINATION;
            agent.add(new MovementTaskComponent(target));
        }
    }

    private void handleIdleState(Entity agent, HaulComponent hauling) {
        Vector2 agentPosition = agent.getComponent(PositionComponent.class).getPosition();
        GridPoint2 itemPosition = hauling.getItemPickupLocation();
        GridPoint2 target = mapService.findValidInteractionPoint(reduceToCell(agentPosition), itemPosition);

        if (target == null) {
            hauling.setInaccessiblePosition(itemPosition);
            hauling.state = WorkState.FAILED;
            return;
        }

        if (target.equals(reduceToCell(agentPosition))) {
            hauling.state = WorkState.PICKING_UP;
        } else {
            hauling.state = WorkState.MOVING_TO_ITEM;
            agent.add(new MovementTaskComponent(target));
        }
    }
}


