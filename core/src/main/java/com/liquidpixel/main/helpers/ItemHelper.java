package com.liquidpixel.main.helpers;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.utils.Mappers;

public class ItemHelper {
    Entity entity;

    public ItemHelper(Entity entity) {
        this.entity = entity;
    }

    public static String getName(Entity entity) {
        if (Mappers.item.has(entity)) {
            return Mappers.item.get(entity).getName();
        } else if (Mappers.agent.has(entity)) {
            return Mappers.agent.get(entity).getId();
        } else {
            return "";
        }
    }
}
