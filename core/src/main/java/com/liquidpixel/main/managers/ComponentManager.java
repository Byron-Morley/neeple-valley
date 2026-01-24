package com.liquidpixel.main.managers;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.interfaces.DisposableComponent;

public class ComponentManager {
    public static <T extends Component> void replace(Entity entity, T newComponent, Class<T> componentClass) {
        Component oldComponent = entity.getComponent(componentClass);
        if (oldComponent instanceof DisposableComponent) {
            ((DisposableComponent) oldComponent).dispose();
        }
        entity.add(newComponent);
    }
}
