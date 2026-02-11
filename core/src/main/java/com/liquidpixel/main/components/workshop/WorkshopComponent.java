package com.liquidpixel.main.components.workshop;

import com.badlogic.ashley.core.Component;

public class WorkshopComponent implements Component {

    String resource;

    public WorkshopComponent(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }
}
