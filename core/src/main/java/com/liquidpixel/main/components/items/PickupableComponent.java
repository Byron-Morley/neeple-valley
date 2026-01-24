package com.liquidpixel.main.components.items;

import com.badlogic.ashley.core.Component;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PickupableComponent implements Component {
    public PickupableComponent() {}
}
