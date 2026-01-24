package com.liquidpixel.main.utils.events;

import com.badlogic.ashley.core.Entity;

public class StorageLinkEvent {
    Entity entity;
    String listType;

    public StorageLinkEvent(Entity entity, String listType) {
        this.entity = entity;
        this.listType = listType;
    }

    public Entity getEntity() {
        return entity;
    }

    public String getListType() {
        return listType;
    }
}
