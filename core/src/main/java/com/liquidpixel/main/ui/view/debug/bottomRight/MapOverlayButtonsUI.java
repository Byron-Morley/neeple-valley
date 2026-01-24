package com.liquidpixel.main.ui.view.debug.bottomRight;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.liquidpixel.main.utils.events.Messages;


public class MapOverlayButtonsUI extends Table {
    public MapOverlayButtonsUI(Skin skin) {
        super(skin);

        CheckBox toggleGridCheckbox = new CheckBox("", skin);
        CheckBox toggleChunkBorderCheckbox = new CheckBox("", skin);
        CheckBox togglePathNodes = new CheckBox("", skin);

        toggleGridCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MessageManager.getInstance().dispatchMessage(Messages.TOGGLE_GRID);
            }
        });
        toggleChunkBorderCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MessageManager.getInstance().dispatchMessage(Messages.TOGGLE_CHUNK_BORDER);
            }
        });
        togglePathNodes.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MessageManager.getInstance().dispatchMessage(Messages.TOGGLE_PATH_NODES);
            }
        });

        this.add(toggleGridCheckbox).padRight(10);
        this.add(toggleChunkBorderCheckbox).padRight(10);
        this.add(togglePathNodes);
    }
}
