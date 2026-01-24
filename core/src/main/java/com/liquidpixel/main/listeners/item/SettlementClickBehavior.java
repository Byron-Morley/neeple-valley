package com.liquidpixel.main.listeners.item;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.selection.api.IClickBehavior;
import com.liquidpixel.main.interfaces.services.ISelectionService;

public class SettlementClickBehavior implements IClickBehavior {

    ISelectionService selectionService;

    public SettlementClickBehavior(ISelectionService selectionService) {
        this.selectionService = selectionService;
    }

    @Override
    public void onClick(Entity entity) {
//        selectionService.setSelectedSettlement(entity);
    }
}
