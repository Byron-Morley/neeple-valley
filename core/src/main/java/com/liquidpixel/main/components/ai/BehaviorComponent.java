package com.liquidpixel.main.components.ai;

import com.badlogic.ashley.core.Component;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BehaviorComponent implements Component {

    @JsonProperty
    String behaviorFileName;


    public BehaviorComponent() {
    }

    @JsonCreator
    public BehaviorComponent(String behaviorFileName) {
        this.behaviorFileName = behaviorFileName;
    }

    public String getBehaviorFileName() {
        return behaviorFileName;
    }
}
