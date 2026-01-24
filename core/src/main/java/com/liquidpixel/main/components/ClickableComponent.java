package com.liquidpixel.main.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.liquidpixel.selection.api.IClickBehavior;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClickableComponent implements Component {

    @JsonIgnore
    IClickBehavior clickBehavior;

    @JsonProperty
    String behaviorId;

    @JsonCreator
    public ClickableComponent(IClickBehavior behavior, String behaviorId) {
        this.clickBehavior = behavior;
        this.behaviorId = behaviorId;
    }

    public void handleClick(Entity entity) {
        if(clickBehavior != null) {
            clickBehavior.onClick(entity);
        }
    }

    @JsonProperty
    public String getBehaviorId() {
        return behaviorId;
    }
}
