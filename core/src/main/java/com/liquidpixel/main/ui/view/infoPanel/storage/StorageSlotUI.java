package com.liquidpixel.main.ui.view.infoPanel.storage;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class StorageSlotUI extends VisTextButton {

    final VisLabel quantityLabel;

    public StorageSlotUI(Image image, String text) {
        super("", "slot");

        VisTable content = new VisTable();
        quantityLabel = new VisLabel(text);

        content.center();
        content.add(image).size(32f).pad(4f).padTop(8f).center();
        content.row();
        content.add(quantityLabel).padBottom(4f).center();

        add(content).center().expand().fill();
    }
}
