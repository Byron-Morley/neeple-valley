package com.liquidpixel.main.components.ai;

import com.badlogic.ashley.core.Component;

public class BehaviorComponent implements Component {

    String behaviorFileName;

    public BehaviorComponent() {
    }

    public BehaviorComponent(String behaviorFileName) {
        this.behaviorFileName = behaviorFileName;
    }

    public String getBehaviorFileName() {
        return behaviorFileName;
    }
}
