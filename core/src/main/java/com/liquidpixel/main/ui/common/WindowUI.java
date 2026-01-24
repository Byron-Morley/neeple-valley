package com.liquidpixel.main.ui.common;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.liquidpixel.main.interfaces.IGet;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisWindow;


public abstract class WindowUI extends VisWindow implements IGet<Group> {
    Button closeButton;

    public WindowUI(String title) {
        super(title);

//        Skin skin = VisUI.getSkin();
//
//        Table titleTable = getTitleTable();
//        titleTable.clear(); // Clear the default title table setup
//
//        // Get the title label
//        Label titleLabel = this.getTitleLabel();
//
//        // Apply custom style
//        Label.LabelStyle customStyle = new Label.LabelStyle(skin.get("title", Label.LabelStyle.class));
//        titleLabel.setStyle(customStyle);
//
//        // Create a container for the title with padding
//        Table subTitleTable = new Table();
//        subTitleTable.setDebug(true); // For debugging
//
//        // Add the title label to the subTitleTable with padding
//        subTitleTable.add(titleLabel).pad(5, 15, 5, 15);
//
//        // Add the subTitleTable to the titleTable with center alignment and expand to fill width
//        titleTable.add(subTitleTable).expandX().center();

    }

    @Override
    public VisWindow get() {
        return this;
    }

    public Button getCloseButton() {
        return closeButton;
    }

    public void setCloseButton(Button closeButton) {
        this.closeButton = closeButton;
    }

    public void overrideCloseListener(ClickListener clickListener) {
        closeButton.removeListener(getCloseButton().getListeners().get(0));
        closeButton.addListener(clickListener);
    }
}
