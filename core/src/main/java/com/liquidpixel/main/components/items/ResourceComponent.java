package com.liquidpixel.main.components.items;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.ui.view.selection.SelectionUI;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceComponent implements Component {

    SelectionUI selectionUI;

    float harvestTime;

    public ResourceComponent(float harvestTime) {
        this.harvestTime = harvestTime;
    }

    public float getHarvestTime() {
        return harvestTime;
    }


    public SelectionUI getSelectionUI() {
        return selectionUI;
    }

    public void setSelectionUI(SelectionUI selectionUI) {
        this.selectionUI = selectionUI;
    }
}
