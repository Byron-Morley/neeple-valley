package com.liquidpixel.main.ui.view.farmUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.liquidpixel.main.interfaces.IGet;
import com.liquidpixel.main.interfaces.Updatable;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.ui.common.ReuseableWindow;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

import static com.liquidpixel.main.screens.WorldScreen.UI_SCALE;

public class FarmingUI extends ReuseableWindow implements IGet<Group>, Updatable {

    ISelectionService selectionService;
    VisTable contentTable;

    public FarmingUI(ISelectionService selectionService) {
        super("Farming");
        this.setMovable(false);
        this.setVisible(false);
        addCloseButton();
        this.selectionService = selectionService;
    }

    public void init() {
        this.setMovable(true);
        this.setVisible(true);

        defaults().pad(5 / UI_SCALE, 5 / UI_SCALE, 5 / UI_SCALE, 5 / UI_SCALE);
        row().fill().expandX().expandY();

        VisTable root = new VisTable();
        add(root);

        contentTable = new VisTable();
        contentTable.defaults().pad(5).size(60);
        root.add(contentTable).fill().expand();




//           ScrollPane scrollPane = new ScrollPanelUI();



        pack();

        setPosition(
            (Gdx.graphics.getWidth() - getWidth()) / 2,
            (Gdx.graphics.getHeight() - getHeight()) / 2
        );
    }

    @Override
    public void update() {
        pack();
    }

    @Override
    public VisWindow get() {
        return this;
    }
}
