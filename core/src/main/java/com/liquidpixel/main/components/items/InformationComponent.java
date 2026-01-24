package com.liquidpixel.main.components.items;

import com.badlogic.ashley.core.Component;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InformationComponent implements Component {

    @JsonProperty
    String label;

    @JsonCreator
    public InformationComponent(
        @JsonProperty("label") String label
    ) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
