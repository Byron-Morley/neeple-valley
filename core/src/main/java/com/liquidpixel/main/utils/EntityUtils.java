package com.liquidpixel.main.utils;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;

public class EntityUtils {
    public static void printEntityComponents(Entity entity) {
        ImmutableArray<Component> components = entity.getComponents();

        for(Component component : components){
            System.out.println(component.getClass().getSimpleName());
        }
    }
}
