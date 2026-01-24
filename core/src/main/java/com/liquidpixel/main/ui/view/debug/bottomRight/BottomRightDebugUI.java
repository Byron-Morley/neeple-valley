package com.liquidpixel.main.ui.view.debug.bottomRight;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.liquidpixel.main.interfaces.IGet;

public class BottomRightDebugUI implements IGet<Group> {
    private Table table;
    private MapOverlayButtonsUI mapOverlayButtonsUI;
    public BottomRightDebugUI(Stage stage, Skin skin) {
        this.table = new Table(skin);
        this.table.setFillParent(true);
        this.table.align(Align.bottomRight);
        this.table.padRight(20);
        this.table.padBottom(20);

        this.mapOverlayButtonsUI = new MapOverlayButtonsUI(skin);

        this.table.add(mapOverlayButtonsUI);
        this.table.row();
    }



    @Override
    public Table get() {
        return this.table;
    }

}
