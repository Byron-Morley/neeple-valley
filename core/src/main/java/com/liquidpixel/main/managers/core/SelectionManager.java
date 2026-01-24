package com.liquidpixel.main.managers.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.TimeUtils;
import com.liquidpixel.main.components.selection.SelectedComponent;
import com.liquidpixel.main.interfaces.managers.ISelectionManager;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.main.services.SelectionService;
import com.liquidpixel.main.services.SettlementService;
import com.liquidpixel.main.services.items.StorageService;
import com.liquidpixel.main.ui.common.EntitySelectionUI;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.selection.api.IClickAction;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;

import java.util.HashSet;
import java.util.Set;

import static com.liquidpixel.main.managers.EntityIdManager.getEntityName;
import static com.liquidpixel.main.utils.events.Messages.*;

public class SelectionManager implements ISelectionManager {

    Set<Entity> selectedEntities;

    Entity selectedSettlement;
    float selectionTimeout = 0.2f;

    long lastSelectionTime;
    boolean selectionAllowed = true;
    boolean validBuildingSpot = true;

    // New fields for action queues
    private IClickAction leftClickAction = null;

    // Track when the last click was processed
    private long lastLeftClickTime = 0;
    private static final long CLICK_COOLDOWN_MS = 200; // Prevent rapid firing

    IPlayerInputService playerInputService;
    IItemService itemService;
    ISettlementService settlementService;
    IStorageService storageService;
    ISelectionService selectionService;

    public SelectionManager(IPlayerInputService playerInputService, IItemService itemService, ISpriteFactory spriteFactory) {
        this.playerInputService = playerInputService;
        this.itemService = itemService;
        this.selectedEntities = new HashSet<>();
        settlementService = new SettlementService(this, itemService, spriteFactory);
        selectionService = new SelectionService(this, itemService);
        storageService = new StorageService(this, itemService, spriteFactory);
    }

    @Override
    public void setLeftClickAction(IClickAction action) {
        leftClickAction = action;
        MessageManager.getInstance().dispatchMessage(ACTION, "Action: " + leftClickAction.getClass().getSimpleName());
    }

    @Override
    public void clearLeftClickAction() {
        if (leftClickAction != null) {

            IClickAction nextAction = leftClickAction.exit();

            if (nextAction != null) {
                leftClickAction = nextAction;
                MessageManager.getInstance().dispatchMessage(ACTION, "Action: " + leftClickAction.getClass().getSimpleName());
            } else {
                leftClickAction = null;
                MessageManager.getInstance().dispatchMessage(ACTION, "Action: No Action");
            }
        }
    }


    @Override
    public boolean executeLeftClickAction(GridPoint2 position, boolean isNewClick) {
        long currentTime = System.currentTimeMillis();

        // Only process if it's a new click or enough time has passed since last execution
        if (leftClickAction != null && (isNewClick || currentTime - lastLeftClickTime > CLICK_COOLDOWN_MS)) {
            lastLeftClickTime = currentTime;

            // Execute the action and get potential follow-up action
            IClickAction nextAction = leftClickAction.execute(position);

            // If this was a one-shot action, clear it or replace with follow-up
            if (leftClickAction.isOneShot()) {
                leftClickAction = nextAction; // This might be null
            } else if (nextAction != null) {
                // If not one-shot but has a follow-up, replace with follow-up
                leftClickAction = nextAction;
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean hasLeftClickActionQueued() {
        return leftClickAction != null;
    }

    @Override
    public boolean isSelectionAllowed() {
        if (selectionAllowed) {
            long elapsedTime = TimeUtils.timeSinceMillis(lastSelectionTime);
            return elapsedTime / 1000f >= selectionTimeout;
        }
        return false;
    }

    public void setSelectionAllowed(boolean selectionAllowed) {
        if (selectionAllowed) {
            lastSelectionTime = TimeUtils.millis();
        }
        this.selectionAllowed = selectionAllowed;
    }

    public ISelectionService getSelectionService() {
        return selectionService;
    }

    @Override
    public ISettlementService getSettlementService() {
        return settlementService;
    }

    @Override
    public void addSelectedEntity(Entity entity) {
        selectedEntities.add(entity);
        entity.add(new SelectedComponent());
        logSelected();
    }

    @Override
    public void removeSelectedEntity(Entity entity) {
        selectedEntities.remove(entity);
        unselectEntity(entity);
        logSelected();
    }

    private void logSelected() {
        if (selectedEntities.isEmpty()) {
            MessageManager.getInstance().dispatchMessage(SELECTED_ENTITY, "Entity: No Selection");
        } else {

            Entity selectedEntity = null;
            try {
                selectedEntity = selectedEntities.stream().findFirst().get();
            } catch (Exception e) {
                System.out.println("Error getting selected entity");
            }
            MessageManager.getInstance().dispatchMessage(SELECTED_ENTITY, "Entity: " + getEntityName(selectedEntity));
        }
    }

    private void unselectEntity(Entity entity) {
        if (entity != null && Mappers.selected.has(entity)) {
            SelectedComponent selectedComponent = Mappers.selected.get(entity);
            EntitySelectionUI entitySelectionUI = selectedComponent.getEntitySelectionUI();
            if (entitySelectionUI != null) entitySelectionUI.remove();
            entity.remove(SelectedComponent.class);
        }
    }

    @Override
    public void clearSelected() {
        clearSelected(null);
    }

    @Override
    public void clearSelected(Entity entity) {
        for (Entity e : selectedEntities) {
            if (e != entity) {
                unselectEntity(e);
            }
        }
        selectedEntities.clear();
        selectedEntities.add(entity);
    }

    public boolean isValidBuildingSpot() {
        return validBuildingSpot;
    }

    public void setValidBuildingSpot(boolean validBuildingSpot) {
        this.validBuildingSpot = validBuildingSpot;
    }

    public Entity getSelectedSettlement() {
        return selectedSettlement;
    }

    public void setSelectedSettlement(Entity selectedSettlement) {
        this.selectedSettlement = selectedSettlement;
    }

    public IStorageService getStorageService() {
        return storageService;
    }
}
