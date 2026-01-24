package com.liquidpixel.main.ui.common;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.liquidpixel.main.engine.GameState;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisWindow;

public class ReuseableWindow extends VisWindow {

    boolean unPauseOnClose = true;

    public ReuseableWindow(String title) {
        super(title);
        defaults();

        Skin skin = VisUI.getSkin();

        Table titleTable = getTitleTable();
        titleTable.clear(); // Clear the default title table setup

        // Get the title label
        Label titleLabel = this.getTitleLabel();

        // Apply custom style
        Label.LabelStyle customStyle = new Label.LabelStyle(skin.get("title", Label.LabelStyle.class));
        titleLabel.setStyle(customStyle);

        // Create a container for the title with padding
        Table subTitleTable = new Table();
        subTitleTable.setDebug(false); // For debugging

        subTitleTable.add(titleLabel).padTop(-12);

        // Add the subTitleTable to the titleTable with center alignment and expand to fill width
        titleTable.add(subTitleTable).expandX().center();

    }

    public void addCloseButton() {
        Label titleLabel = this.getTitleLabel();
        Table titleTable = this.getTitleTable();
        VisImageButton closeButton = new VisImageButton("close-window");
        titleTable.add(closeButton).padRight(-this.getPadRight() - 4).padBottom(12f);
        closeButton.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (unPauseOnClose) GameState.setPaused(false);
                setVisible(false);
            }
        });
        closeButton.addListener(new ClickListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.cancel();
                return true;
            }
        });
        if (titleLabel.getLabelAlign() == 1 && titleTable.getChildren().size == 2) {
            titleTable.getCell(titleLabel).padLeft(closeButton.getWidth() * 2.0F);
        }
    }

    public void setUnPauseOnClose(boolean unPauseOnClose) {
        this.unPauseOnClose = unPauseOnClose;
    }
}
