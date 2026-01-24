package com.liquidpixel.main.ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;


public class UiTestGame extends Game {
    private UiTestScreen testScreen;
    private long lastReloadTime = 0;
    private static final long RELOAD_COOLDOWN = 1000; // 1 second cooldown

    @Override
    public void create() {
        testScreen = new UiTestScreen();
        setScreen(testScreen);
    }


    @Override
    public void render() {
        // Check for F5 key press to reload
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastReloadTime > RELOAD_COOLDOWN) {
                lastReloadTime = currentTime;
                reloadUi();
            }
        }

        super.render();

        if (Gdx.input.isKeyPressed(Input.Keys.F5)) {
            this.dispose();
            create();
            this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    private void reloadUi() {
        Gdx.app.log("UiTest", "Reloading UI...");
        try {
            // Reload UI setup
            testScreen.setupUi();
            Gdx.app.log("UiTest", "UI reloaded successfully");
        } catch (Exception e) {
            Gdx.app.error("UiTest", "Error reloading UI", e);
        }
    }
}
