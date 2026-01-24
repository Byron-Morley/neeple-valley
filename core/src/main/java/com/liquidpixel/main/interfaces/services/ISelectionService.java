package com.liquidpixel.main.interfaces.services;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.selection.api.IClickAction;

public interface ISelectionService {

    void setSelectionAllowed(boolean selectionAllowed);

    boolean isSelectionAllowed();

    void clearSelected();

    //clears all then adds this entity
    void clearSelected(Entity entity);

    void addSelectedEntity(Entity entity);

    boolean isValidBuildingSpot();

    void setValidBuildingSpot(boolean validBuildingSpot);

    Entity getSelectedSettlement();

    void setSelectedSettlement(Entity selectedSettlement);

    boolean executeNextLeftClickAction(GridPoint2 cursorPosition, boolean isNewClick);

    boolean hasLeftClickActionQueued();

    void cancelLeftClickAction();

    void addLeftClickAction(IClickAction action);

    void clearLeftClickActions();

    void markForPickingUp(Entity entity, GridPoint2 position);
}
