package com.liquidpixel.main.components.inits;

import com.badlogic.ashley.core.Component;

public class ClickBehaviorInitComponent implements Component {

    String behaviorId;

    public ClickBehaviorInitComponent(String behaviorId) {
        this.behaviorId = behaviorId;
    }

    public String getBehaviorId() {
        return behaviorId;
    }
}
