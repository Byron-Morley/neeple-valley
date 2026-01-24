package com.liquidpixel.main.managers;

import com.badlogic.gdx.Screen;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.interfaces.Renderable;
import com.liquidpixel.main.screens.WorldScreen;


public class ScreenManager implements Renderable {
    private Screen currentScreen;
    public Screen getCurrentScreen() {
        return currentScreen;
    }

    public void setCurrentScreen(Screen currentScreen) {
        if(this.currentScreen != null)
            this.currentScreen.dispose();

        this.currentScreen = currentScreen;
    }

    @Override
    public void render(float delta) {
        this.currentScreen.render(delta);
    }


    @Override
    public void dispose() {

    }


}
