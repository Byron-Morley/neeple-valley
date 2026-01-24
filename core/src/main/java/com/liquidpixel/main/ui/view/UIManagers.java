package com.liquidpixel.main.ui.view;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.liquidpixel.main.engine.GameResources;

public class UIManagers {
    protected Stage stage;
    protected Engine engine;

    public UIManagers(Stage stage) {
        this.stage = stage;
        this.engine = GameResources.get().getEngine();
    }
}
