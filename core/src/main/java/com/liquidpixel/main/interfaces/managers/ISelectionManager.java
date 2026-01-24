package com.liquidpixel.main.interfaces.managers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.selection.api.IClickAction;
import com.liquidpixel.main.interfaces.services.IStorageService;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.interfaces.services.ISettlementService;

public interface ISelectionManager {

    ISelectionService getSelectionService();

    ISettlementService getSettlementService();

    void addSelectedEntity(Entity entity);

    void removeSelectedEntity(Entity entity);

    void clearSelected();

    void clearSelected(Entity entity);

    void setSelectionAllowed(boolean selectionAllowed);

    boolean isSelectionAllowed();

    boolean isValidBuildingSpot();

    void setValidBuildingSpot(boolean validBuildingSpot);

    Entity getSelectedSettlement();

    void setSelectedSettlement(Entity selectedSettlement);

    IStorageService getStorageService();

    boolean executeLeftClickAction(GridPoint2 position, boolean isNewClick);

    void setLeftClickAction(IClickAction action);

    void clearLeftClickAction();

    boolean hasLeftClickActionQueued();
}
