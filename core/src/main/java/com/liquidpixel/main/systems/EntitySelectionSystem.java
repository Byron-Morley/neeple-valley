package com.liquidpixel.main.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.liquidpixel.main.components.selection.SelectedComponent;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.ui.common.EntitySelectionUI;
import com.liquidpixel.main.utils.Mappers;

public class EntitySelectionSystem extends IteratingSystem {

    IItemService itemService;

    public EntitySelectionSystem(IItemService itemService) {
        super(Family.all(SelectedComponent.class).get());
        this.itemService = itemService;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        SelectedComponent selected = Mappers.selected.get(entity);
        if (selected.getEntitySelectionUI() == null) {

            EntitySelectionUI entitySelectionUI = new EntitySelectionUI();
            entitySelectionUI.create(itemService);
            selected.setEntitySelectionUI(entitySelectionUI);

        } else {
            selected.getEntitySelectionUI().update(entity);
        }
    }
}
