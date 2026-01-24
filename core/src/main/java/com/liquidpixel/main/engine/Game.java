package com.liquidpixel.main.engine;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.liquidpixel.main.managers.ScreenManager;
import com.liquidpixel.main.screens.WorldScreen;
import com.kotcrab.vis.ui.VisUI;


public class Game extends ApplicationAdapter {

    private Engine engine;
    private SpriteBatch batch;
    private ScreenManager screenManager;
    GLProfiler glProfiler;
    ShapeRenderer shapeRenderer;
    static AssetManager assetManager;

    @Override
    public void create() {
        assetManager = new AssetManager();
        engine = new Engine();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        String skinPath = "skin/skin";
        assetManager.load(skinPath + ".json", Skin.class);
        assetManager.finishLoading();

        Skin skin = assetManager.get(skinPath + ".json", Skin.class);

        VisUI.load(skin);

        initializeScreen();
        glProfiler = new GLProfiler(Gdx.graphics);
        glProfiler.enable();

    }

    private void initializeScreen() {
        screenManager = new ScreenManager();
        screenManager.setCurrentScreen(new WorldScreen(new GameResources(engine, batch, shapeRenderer)));
    }

    @Override
    public void render() {
        if (!GameState.isPaused()) clearScreen();

        screenManager.render(Gdx.graphics.getDeltaTime());

//		Gdx.app.log("drawCalls", String.valueOf(glProfiler.getDrawCalls()));
//		Gdx.app.log("calls", String.valueOf(glProfiler.getCalls()));
//		Gdx.app.log("textureBindings", String.valueOf(glProfiler.getTextureBindings()));
//		Gdx.app.log("shaderSwitches ", String.valueOf(glProfiler.getShaderSwitches()));
//		Gdx.app.log("vertexCount ", String.valueOf(glProfiler.getVertexCount()));

        if (Gdx.input.isKeyPressed(Input.Keys.F5)) {
            this.dispose();
            create();
            this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        glProfiler.reset();
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
//        batch.dispose();
//        shapeRenderer.dispose();
        super.dispose();
    }

    public ScreenManager getScreenManager() {
        return screenManager;
    }

    public void setBatch(SpriteBatch batch) {
        this.batch = batch;
    }
}
