package com.liquidpixel.main.tools;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.engine.GameState;
import com.liquidpixel.main.managers.ScreenManager;
import com.liquidpixel.main.screens.ScenarioBuilder;
import com.kotcrab.vis.ui.VisUI;

public class ScenarioBuilderApplication extends ApplicationAdapter {
    private Engine engine;
    private SpriteBatch batch;
    private ScreenManager screenManager;
    private ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        engine = new Engine();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        Skin skin = new Skin(Gdx.files.internal("skin/skin.json"));
        VisUI.load(skin);

        initializeScreen();
    }

    private void initializeScreen() {
        screenManager = new ScreenManager();
        screenManager.setCurrentScreen(new ScenarioBuilder(new GameResources(engine, batch, shapeRenderer)));
    }

    @Override
    public void render() {
        if (!GameState.isPaused()) clearScreen();
        screenManager.render(Gdx.graphics.getDeltaTime());

        if (Gdx.input.isKeyPressed(Input.Keys.F5)) {
            this.dispose();
            create();
            this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if (screenManager.getCurrentScreen() != null)
            screenManager.getCurrentScreen().resize(width, height);
    }

    @Override
    public void dispose() {
        if (screenManager.getCurrentScreen() != null)
            screenManager.getCurrentScreen().dispose();
        VisUI.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        super.dispose();
    }
}
