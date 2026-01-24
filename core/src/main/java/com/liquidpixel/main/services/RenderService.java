package com.liquidpixel.main.services;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.interfaces.IRenderService;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.main.components.render.FadeComponent;

public class RenderService extends Service implements IRenderService {

    private static final float DEFAULT_FADE_DURATION = 1.0f;

    @Override
    public boolean isOpaque(Entity entity) {
        if (Mappers.fade != null && Mappers.fade.has(entity)) {
            FadeComponent fadeComponent = Mappers.fade.get(entity);
            return fadeComponent.getAlpha() >= 1.0f;
        }
        return true;
    }

    @Override
    public void fadeIn(Entity entity) {
        fadeIn(entity, DEFAULT_FADE_DURATION);
    }

    public void fadeIn(Entity entity, float duration) {
        FadeComponent fadeComponent = Mappers.fade.get(entity);
        if (fadeComponent == null) {
            fadeComponent = new FadeComponent();
            entity.add(fadeComponent);
        }
        fadeComponent.startFadeIn(duration);
    }

    @Override
    public void fadeOut(Entity entity) {
        fadeOut(entity, DEFAULT_FADE_DURATION);
    }

    public void fadeOut(Entity entity, float duration) {
        FadeComponent fadeComponent = Mappers.fade.get(entity);
        if (fadeComponent == null) {
            fadeComponent = new FadeComponent();
            entity.add(fadeComponent);
        }
        fadeComponent.startFadeOut(duration);
    }


    public void makeInvisible(Entity entity) {
        if (isFullyVisible(entity)) {
            if (Mappers.fade.has(entity)) {
                FadeComponent fadeComponent = Mappers.fade.get(entity);
                fadeComponent.setAlpha(0.0f);
            } else {
                FadeComponent fadeComponent = new FadeComponent();
                fadeComponent.setAlpha(0.0f);
                entity.add(fadeComponent);
            }
        }
    }

    @Override
    public void makeVisible(Entity entity) {
           if (isOpaque(entity)) {
            if (Mappers.fade.has(entity)) {
                FadeComponent fadeComponent = Mappers.fade.get(entity);
                fadeComponent.setAlpha(1f);
            } else {
                FadeComponent fadeComponent = new FadeComponent();
                fadeComponent.setAlpha(1f);
                entity.add(fadeComponent);
            }
        }
    }

    @Override
    public void setOpacity(Entity entity, float opacity) {
            if (Mappers.fade.has(entity)) {
                FadeComponent fadeComponent = Mappers.fade.get(entity);
                fadeComponent.setAlpha(opacity);
            } else {
                FadeComponent fadeComponent = new FadeComponent();
                fadeComponent.setAlpha(opacity);
                entity.add(fadeComponent);
            }
    }

    @Override
    public boolean isFullyVisible(Entity entity) {
        if (Mappers.fade != null && Mappers.fade.has(entity)) {
            FadeComponent fadeComponent = Mappers.fade.get(entity);
            return fadeComponent.getAlpha() >= 1.0f && !fadeComponent.isFading();
        }
        return true;
    }
}
