package com.liquidpixel.main.services.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.interfaces.managers.IUIManager;
import com.liquidpixel.main.interfaces.ui.IUIService;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.main.services.Service;

import static com.liquidpixel.main.screens.WorldScreen.UI_SCALE;

public class UIService extends Service implements IUIService {

    Stage stage;
    IUIManager uiManager;

    public UIService(IUIManager uiManager) {
        this.uiManager = uiManager;
        this.stage = GameResources.get().getStage();
    }

    public boolean isMouseOverUI() {
        Vector2 mouseCoords = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        mouseCoords.y = stage.getViewport().getScreenHeight() - mouseCoords.y;
        Actor actor = stage.hit(mouseCoords.x / UI_SCALE, mouseCoords.y / UI_SCALE, true);
        return actor != null;
    }

    public int getIndexOfStorageItem(Array<StorageItem> items, String name) {
        for (int i = 0; i < items.size; i++) {
            if (items.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }





}
