package com.liquidpixel.main.model.ui;

import com.liquidpixel.selection.api.IMenuClickBehavior;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.liquidpixel.selection.api.IMenuItem;

import java.util.List;

public class MenuTreeItem implements IMenuItem {

    @JsonProperty
    String label;

    @JsonProperty
    String icon;

    @JsonProperty
    String spawn;

    @JsonProperty
    String name;

    @JsonIgnore
    IMenuClickBehavior clickBehavior;

    @JsonProperty
    List<MenuTreeItem> children;

    @JsonProperty
    String behaviorId;

    public MenuTreeItem() {
    }

    public String getLabel() {
        return label;
    }

    public String getIcon() {
        return icon;
    }

    public String getSpawn() {
        return spawn;
    }

    public List<MenuTreeItem> getChildren() {
        return children;
    }

    public void onClick() {
        if (clickBehavior != null) {
            clickBehavior.onClick(this);
        }
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getBehaviorId() {
        return behaviorId;
    }

    public void setClickBehavior(IMenuClickBehavior clickBehavior) {
        this.clickBehavior = clickBehavior;
    }

    public String getName() {
        return name;
    }
}
