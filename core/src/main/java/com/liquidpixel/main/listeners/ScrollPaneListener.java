package com.liquidpixel.main.listeners;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

public class ScrollPaneListener extends InputListener {
    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        if (pointer == -1) {
            Actor actor = event.getListenerActor();
            if (actor instanceof ScrollPane && actor.getStage() != null) {
                actor.getStage().setScrollFocus(actor);
            }
        }
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        if (pointer == -1) {
            Actor actor = event.getListenerActor();
            if (actor instanceof ScrollPane && actor.getStage() != null) {
                if (toActor == null || !toActor.isDescendantOf(actor)) {
                    if (actor.getStage().getScrollFocus() == actor) {
                        actor.getStage().setScrollFocus(null);
                    }
                }
            }
        }
    }

    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        Actor actor = event.getListenerActor();
        if (actor instanceof ScrollPane && actor.getStage() != null) {
            if (actor.getStage().getScrollFocus() != actor) {
                actor.getStage().setScrollFocus(actor);
            }
        }
        return false;
    }
}
