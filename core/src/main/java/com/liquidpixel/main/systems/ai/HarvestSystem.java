package com.liquidpixel.main.systems.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.core.components.core.StatusComponent;
import com.liquidpixel.main.components.HarvestableComponent;
import com.liquidpixel.main.components.agent.AgentComponent;
import com.liquidpixel.pathfinding.components.MovementTaskComponent;
import com.liquidpixel.main.components.ai.actions.HarvestComponent;
import com.liquidpixel.main.components.ai.actions.WorkTaskComponent;
import com.liquidpixel.main.components.items.EquipableComponent;
import com.liquidpixel.sprite.components.AnimableSpriteComponent;
import com.liquidpixel.main.components.ui.MarkComponent;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.IRecipe;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.managers.EntityInteractionManager;
import com.liquidpixel.core.core.Action;
import com.liquidpixel.core.core.Direction;
import com.liquidpixel.main.model.status.WorkState;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.main.utils.SteeringUtils;

import java.util.List;

public class HarvestSystem extends IteratingSystem {

    ISelectionService selectionService;
    IItemService itemService;
    IWorldMap map;

    public HarvestSystem(IItemService itemService, IWorldMap map, ISelectionService selectionService) {
        super(Family.all(HarvestComponent.class).get());
        this.itemService = itemService;
        this.map = map;
        this.selectionService = selectionService;
    }

    @Override
    protected void processEntity(Entity agent, float delta) {
        HarvestComponent harvest = agent.getComponent(HarvestComponent.class);
        switch (harvest.state) {
            case IDLE:
                handleIdleState(agent, harvest);
                break;
            case MOVING_TO_LOCATION:
                handleMovingState(agent, harvest);
                break;
            case HARVEST:
                handleHarvestState(agent, harvest);
                break;
            case HARVESTING:
                handleHarvestingState(agent, harvest);
                break;
            case COMPLETED, FAILED:
                agent.remove(HarvestComponent.class);
                break;
        }
    }

    private void handleMovingState(Entity agent, HarvestComponent harvest) {
        MovementTaskComponent movement = agent.getComponent(MovementTaskComponent.class);
        if (movement != null && movement.state == MovementTaskComponent.State.FINISHED) {
            harvest.state = WorkState.HARVEST;
        }
    }

    private void handleIdleState(Entity agent, HarvestComponent harvest) {
        Gdx.app.debug("HarvestSystem", "IDLE");

        Vector2 agentPosition = agent.getComponent(PositionComponent.class).getPosition();
        GridPoint2 interactionPoint = harvest.getInteractionPoint();

        if (interactionPoint == null) {
            Gdx.app.debug("HarvestSystem", "cannot find path to harvest location");
            harvest.state = WorkState.FAILED;
            return;
        }

        if (interactionPoint.equals(agentPosition)) {
            harvest.state = WorkState.HARVEST;
        } else {
            agent.add(new MovementTaskComponent(interactionPoint));
            harvest.state = WorkState.MOVING_TO_LOCATION;
        }
    }

    private void handleHarvestState(Entity agent, HarvestComponent harvest) {
        Gdx.app.debug("HarvestSystem", "HARVEST");
        List<Entity> entities = map.getEntitiesAtPosition(harvest.getFocusPoint());
        Entity harvestableItem = entities
            .stream()
            .filter(entity -> Mappers.item.get(entity).getName().equals(harvest.getItemName()))
            .findFirst()
            .orElse(null);

        if (harvestableItem == null || Mappers.harvest.get(harvestableItem) == null) {
            harvest.state = WorkState.FAILED;
            return;
        }

        EntityInteractionManager.getInstance().registerInteraction(harvestableItem, agent);
        HarvestableComponent harvestable = Mappers.harvest.get(harvestableItem);


        //get the correct tool
        Entity toolEntity = itemService.getItem(harvestable.getTool()).build();
        itemService.spawnItem(toolEntity, new GridPoint2(-300, -300));
        Mappers.equipment.get(agent).addEquipment(toolEntity);

        //equip the tool
        AgentComponent agentComponent = Mappers.agent.get(agent);
        agentComponent.setEquipped(toolEntity);

        //set correct harvest animation
        Action action = Mappers.equipable.get(toolEntity).getAction();
        Mappers.status.get(agent).setAction(action);

        //set correct direction
        Direction direction = SteeringUtils.getDirection(Mappers.position.get(agent).getPosition(), harvest.getFocusPoint());
        Mappers.status.get(agent).setDirection(direction);

        //sync the animation
        AnimableSpriteComponent axeAnimableSpriteComponent = Mappers.animableSprite.get(toolEntity);
//        axeAnimableSpriteComponent.setTileType(agentComponent.getId());
//        axeAnimableSpriteComponent.setSynchronized(true);

        AnimableSpriteComponent animableSpriteComponent = Mappers.animableSprite.get(agent);
        animableSpriteComponent.addSynchronisedEntity(toolEntity, agent);

        //start harvesting
        agent.add(new WorkTaskComponent(harvestable.getRecipe()));
        harvest.state = WorkState.HARVESTING;
    }

    private void handleHarvestingState(Entity agent, HarvestComponent harvest) {
        WorkTaskComponent workTask = agent.getComponent(WorkTaskComponent.class);

        //set action for equipped item
        AgentComponent agentComponent = Mappers.agent.get(agent);
        Entity equipped = agentComponent.getEquipped();
        EquipableComponent equipable = Mappers.equipable.get(equipped);
        Mappers.status.get(agent).setAction(equipable.getAction());

        //set status and direction for tool
        StatusComponent status = Mappers.status.get(equipped);
        status.setAction(equipable.getAction());
        status.setDirection(Mappers.status.get(agent).getDirection());


        if (workTask.state == WorkTaskComponent.State.COMPLETED) {
            createOutput(agent, harvest);
            harvestCleanup(agent, harvest);
            harvest.state = WorkState.COMPLETED;
        } else if (workTask.state == WorkTaskComponent.State.FAILED) {
            harvestCleanup(agent, harvest);
            harvest.state = WorkState.FAILED;
        }
    }

    private void harvestCleanup(Entity agent, HarvestComponent harvest) {
        List<Entity> entities = map.getEntitiesAtPosition(harvest.getFocusPoint());
        Entity harvestableItem = entities
            .stream()
            .filter(entity -> Mappers.item.get(entity).getName().equals(harvest.getItemName()))
            .findFirst()
            .orElse(null);

        Mappers.status.get(agent).setAction(Action.STANDING);
        Entity mark = Mappers.mark.get(harvestableItem).getMark();
        harvestableItem.remove(MarkComponent.class);

        //remove tool
        AgentComponent agentComponent = Mappers.agent.get(agent);
        Entity equipped = agentComponent.getEquipped();
        agentComponent.setEquipped(null);
        itemService.dispose(equipped);


        EntityInteractionManager.getInstance().removeInteraction(harvestableItem);
        itemService.dispose(mark);

        itemService.dispose(harvestableItem);
        agent.remove(WorkTaskComponent.class);
    }

    private void createOutput(Entity agent, HarvestComponent harvest) {
        WorkTaskComponent workTask = agent.getComponent(WorkTaskComponent.class);
        IRecipe recipe = workTask.getRecipe();
        recipe.getOutput().forEach(item -> {
            Entity itemEntity = itemService.getItem(item.getName(), item.getQuantity()).build();
            itemService.spawnItem(itemEntity, harvest.getFocusPoint());
//            selectionService.markForPickingUp(itemEntity, Mappers.position.get(itemEntity).getGridPosition());
        });
    }
}
