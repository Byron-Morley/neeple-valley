package com.liquidpixel.main.listeners.spawnMenu;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.actions.PaintMenuAction;
import com.liquidpixel.main.components.render.RenderComponent;
import com.liquidpixel.main.renderposition.PaintRenderPositionStrategy;
import com.liquidpixel.selection.api.IMenuClickBehavior;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.model.ui.MenuTreeItem;

public class PaintTerrainClickListener implements IMenuClickBehavior {
    private final ISelectionService selectionService;
    private final IItemService itemService;
    Entity ghost;

    public PaintTerrainClickListener(
        ISelectionService selectionService,
        IItemService itemService
    ) {
        this.selectionService = selectionService;
        this.itemService = itemService;
    }

    @Override
    public void onClick(MenuTreeItem menuTreeItem) {
//        Gdx.input.setCursorCatched(true);
        selectionService.clearLeftClickActions();
        ghost = setupGhost();
        selectionService.addLeftClickAction(new PaintMenuAction(selectionService, menuTreeItem.getName(), menuTreeItem.getSpawn(), ghost));
    }

    private Entity setupGhost() {
        Entity ghost = itemService.getItem("ui/ghost").build();
        itemService.spawnAndPickupItem(ghost);
        selectionService.setSelectionAllowed(false);
        RenderComponent renderComponent = ghost.getComponent(RenderComponent.class);
        renderComponent.setRenderPositionStrategy(new PaintRenderPositionStrategy());
        return ghost;
    }
}


