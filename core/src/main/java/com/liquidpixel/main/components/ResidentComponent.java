package com.liquidpixel.main.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class ResidentComponent implements Component {

    Entity house;

    public ResidentComponent(Entity house) {
        this.house = house;
    }

    public Entity getHouse() {
        return house;
    }
}
