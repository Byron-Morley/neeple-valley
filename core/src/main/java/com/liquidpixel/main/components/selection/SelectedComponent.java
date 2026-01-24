package com.liquidpixel.main.components.selection;

import com.badlogic.ashley.core.Component;
import com.liquidpixel.main.ui.common.EntitySelectionUI;

public class SelectedComponent implements Component {

    EntitySelectionUI entitySelectionUI;

    public EntitySelectionUI getEntitySelectionUI() {
        return entitySelectionUI;
    }

    public void setEntitySelectionUI(EntitySelectionUI entitySelectionUI) {
        this.entitySelectionUI = entitySelectionUI;
    }
}
