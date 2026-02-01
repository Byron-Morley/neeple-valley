package com.liquidpixel.main.helpers;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.core.engine.GameResources;
import com.liquidpixel.main.components.agent.AgentComponent;
import com.liquidpixel.main.components.render.RenderComponent;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.sprite.components.AnimableSpriteComponent;

public class EquipHelper {

    Entity agent;
    Entity tool;

    public EquipHelper(Entity agent, Entity tool) {
        this.agent = agent;
        this.tool = tool;
    }

    public void equip() {
        Mappers.equipment.get(agent).addEquipment(tool);

        AgentComponent agentComponent = Mappers.agent.get(agent);
        agentComponent.setEquipped(tool);

        RenderComponent renderComponent = Mappers.render.get(tool);
        renderComponent.setCentered(true);

        AnimableSpriteComponent animableSpriteComponent = Mappers.animableSprite.get(agent);
        animableSpriteComponent.addSynchronisedEntity(tool, agent);
    }

    public void unequip() {
        Mappers.equipment.get(agent).removeEquipment(tool);

        AgentComponent agentComponent = Mappers.agent.get(agent);
        agentComponent.setEquipped(null);

        AnimableSpriteComponent animableSpriteComponent = Mappers.animableSprite.get(agent);
        animableSpriteComponent.removeSynchronisedEntity(tool);
        GameResources.get().getEngine().removeEntity(tool);
    }
}
