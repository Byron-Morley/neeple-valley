package com.liquidpixel.main.ui.view.menu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.liquidpixel.main.interfaces.IGet;
import com.kotcrab.vis.ui.widget.VisTable;

public class SecondaryMenuBlock extends VisTable implements IGet<Group> {
    private static final float TOTAL_HEIGHT = 80f;
    private static final float PADDING = 10f;
    private final VisTable menu;

    public SecondaryMenuBlock() {
        setFillParent(true);
        setVisible(false);
        bottom();
        VisTable menuBar = new VisTable();
        menu = new VisTable();
        menuBar.add(menu).expandX().center();

        add(menuBar)
            .height(TOTAL_HEIGHT)
            .expandX()
            .fillX()
            .padBottom(80f);
    }

    @Override
    public VisTable get() {
        return menu;
    }
}
