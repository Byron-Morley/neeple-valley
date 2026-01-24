package com.liquidpixel.main.components.ui;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class MarkComponent implements Component {

    Entity mark;

    public MarkComponent(Entity mark) {
        this.mark = mark;
    }

    public Entity getMark() {
        return mark;
    }
}
