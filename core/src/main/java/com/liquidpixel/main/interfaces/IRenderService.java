package com.liquidpixel.main.interfaces;

import com.badlogic.ashley.core.Entity;

public interface IRenderService {

    boolean isOpaque(Entity entity);
    void fadeIn(Entity entity, float duration);
    void fadeIn(Entity entity);
    void fadeOut(Entity entity, float duration);
    void fadeOut(Entity entity);
    boolean isFullyVisible(Entity entity);
    void makeInvisible(Entity entity);
    void makeVisible(Entity entity);
    void setOpacity(Entity entity, float opacity);
}
