package com.liquidpixel.main.components.selection;

import com.badlogic.ashley.core.Component;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SelectableEntityComponent implements Component {

    public SelectableEntityComponent() {
    }
}
