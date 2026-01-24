package com.liquidpixel.main.services;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.AssetComponent;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.components.storage.ProviderComponent;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.components.ui.MarkComponent;
import com.liquidpixel.selection.api.IClickAction;
import com.liquidpixel.main.interfaces.managers.ISelectionManager;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.utils.Mappers;


public class SelectionService extends Service implements ISelectionService {

    ISelectionManager selectionManager;
    IItemService itemService;

    public SelectionService(ISelectionManager selectionManager, IItemService itemService) {
        this.selectionManager = selectionManager;
        this.itemService = itemService;
    }

    @Override
    public boolean isSelectionAllowed() {
        return selectionManager.isSelectionAllowed();
    }

    @Override
    public void setSelectionAllowed(boolean selectionAllowed) {
        selectionManager.setSelectionAllowed(selectionAllowed);
    }

    @Override
    public boolean executeNextLeftClickAction(GridPoint2 cursorPosition, boolean isNewClick) {
        return selectionManager.executeLeftClickAction(cursorPosition, isNewClick);
    }

    @Override
    public boolean hasLeftClickActionQueued() {
        return selectionManager.hasLeftClickActionQueued();
    }

    @Override
    public void cancelLeftClickAction() {
        selectionManager.clearLeftClickAction();
    }

    @Override
    public void addLeftClickAction(IClickAction action) {
        selectionManager.setLeftClickAction(action);
    }

    @Override
    public void clearLeftClickActions() {
        selectionManager.clearLeftClickAction();
    }

    @Override
    public void clearSelected() {
        selectionManager.clearSelected();
    }

    @Override
    public void clearSelected(Entity entity) {
        selectionManager.clearSelected(entity);
    }

    @Override
    public void addSelectedEntity(Entity entity) {
        this.selectionManager.addSelectedEntity(entity);
    }

    public void removeSelectedEntity(Entity entity) {
        this.selectionManager.removeSelectedEntity(entity);
    }

    @Override
    public boolean isValidBuildingSpot() {
        return selectionManager.isValidBuildingSpot();
    }

    @Override
    public void setValidBuildingSpot(boolean validBuildingSpot) {
        selectionManager.setValidBuildingSpot(validBuildingSpot);
    }

    @Override
    public Entity getSelectedSettlement() {
        return selectionManager.getSelectedSettlement();
    }

    @Override
    public void setSelectedSettlement(Entity selectedSettlement) {
        selectionManager.setSelectedSettlement(selectedSettlement);
    }

    public void markForPickingUp(Entity entity, GridPoint2 position) {

        //add mark to pickup
        Entity cross = itemService.getItem("ui/cross").build();
        itemService.spawnItem(cross, position);
        MarkComponent mark = new MarkComponent(cross);
        entity.add(mark);

        //get player settlement
        SettlementComponent settlement = Mappers.settlement.get(getSelectedSettlement());
        StorageComponent storage = Mappers.storage.get(entity);
        storage.setOneUse(true);

        entity.add(new ProviderComponent());
        entity.add(new AssetComponent(getSelectedSettlement()));
        settlement.addAsset(entity, getSelectedSettlement());
    }

}
