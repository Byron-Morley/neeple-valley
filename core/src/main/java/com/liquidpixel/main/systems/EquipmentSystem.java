package com.liquidpixel.main.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.core.components.core.StatusComponent;
import com.liquidpixel.main.components.agent.AgentComponent;
import com.liquidpixel.main.components.items.EquipableComponent;
import com.liquidpixel.main.components.items.EquipmentComponent;
import com.liquidpixel.sprite.components.AnimableSpriteComponent;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.sprite.components.RefreshSpriteRequirementComponent;
import java.util.List;

public class EquipmentSystem extends IteratingSystem {


    public EquipmentSystem() {
        super(Family.all(AgentComponent.class, EquipmentComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AgentComponent agentComponent = Mappers.agent.get(entity);
        EquipmentComponent equipmentComponent = Mappers.equipment.get(entity);

        if(equipmentComponent.hasEquipment()) {
            PositionComponent positionComponent = Mappers.position.get(entity);
            Vector2 agentPosition = positionComponent.getPosition();

            List<Entity> equipment = equipmentComponent.getEquipment();
            for(Entity item : equipment) {
                // Handle positioning (existing functionality)
                PositionComponent itemPositionComponent = Mappers.position.get(item);
                itemPositionComponent.setPosition(agentPosition);

                // Handle status synchronization
//                syncStatusWithAgent(entity, item);

                // Handle initial tool setup
//                setupToolForFirstTime(entity, item);
            }
        }
    }

    private void syncStatusWithAgent(Entity agent, Entity tool) {
        if (Mappers.status.has(agent) && Mappers.status.has(tool)) {
            StatusComponent agentStatus = Mappers.status.get(agent);
            StatusComponent toolStatus = Mappers.status.get(tool);


            // Sync action and direction from agent to tool
            toolStatus.setAction(agentStatus.getAction());
            toolStatus.setDirection(agentStatus.getDirection());
        }
    }

    private void setupToolForFirstTime(Entity agent, Entity tool) {
        // Set up animation synchronization if not already done
        if (Mappers.animableSprite.has(tool) && Mappers.agent.has(agent)) {
            AnimableSpriteComponent animableSpriteComponent = Mappers.animableSprite.get(tool);
            AgentComponent agentComponent = Mappers.agent.get(agent);
            System.out.println("tool not setup");
        }

        // Add refresh sprite requirement if tool doesn't have animations loaded yet
        if (Mappers.animableSprite.has(tool)) {
            AnimableSpriteComponent toolAnimComponent = Mappers.animableSprite.get(tool);
            if (toolAnimComponent.getTexturesToAnimations() == null || toolAnimComponent.getTexturesToAnimations().isEmpty()) {
                if (tool.getComponent(RefreshSpriteRequirementComponent.class) == null) {
                    tool.add(new RefreshSpriteRequirementComponent());
                }
            }
        }

        // Set agent action based on equipped tool's action (for initial equip)
        if (Mappers.equipable.has(tool) && Mappers.status.has(agent)) {
            EquipableComponent equipableComponent = Mappers.equipable.get(tool);
            StatusComponent agentStatus = Mappers.status.get(agent);

            // Only set if agent is in a default state (like STANDING)
            if (agentStatus.getAction().toString().contains("STANDING") || agentStatus.getAction().toString().equals("IDLE")) {
                agentStatus.setAction(equipableComponent.getAction());

                // Also sync this action to the tool
                if (Mappers.status.has(tool)) {
                    Mappers.status.get(tool).setAction(equipableComponent.getAction());
                }
            }
        }
    }
}
