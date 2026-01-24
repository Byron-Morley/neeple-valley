package com.liquidpixel.main.listeners;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CloseListener extends ClickListener {

    private Window window;

    public CloseListener(Window window) {
        this.window = window;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        this.window.remove();
    }
}
