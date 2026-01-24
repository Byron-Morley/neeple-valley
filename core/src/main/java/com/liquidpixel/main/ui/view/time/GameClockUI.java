package com.liquidpixel.main.ui.view.time;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.liquidpixel.main.engine.GameClock;
import com.liquidpixel.main.engine.GameState;
import com.liquidpixel.main.interfaces.IGet;
import com.liquidpixel.main.interfaces.Updatable;
import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.main.interfaces.ui.IResizePosition;
import com.liquidpixel.main.ui.common.ReuseableWindow;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

import static com.liquidpixel.main.screens.WorldScreen.UI_SCALE;

public class GameClockUI extends ReuseableWindow implements IGet<Group>, Updatable, IResizePosition {
    private VisLabel clockLabel;
    private VisTable contentTable;
    IMapService mapService;

    public GameClockUI(IMapService mapService) {
        super("Game Clock");
        this.mapService = mapService;
        init();
    }

    private void init() {
        setMovable(false);
        setVisible(true);
        addCloseButton();

        defaults().pad(10);
        row().fill().expandX().expandY();

        VisTable root = new VisTable();
        add(root);

        // Clock display - now shows both days and hours
        clockLabel = new VisLabel("");
        clockLabel.setFontScale(1.5f);
        clockLabel.setAlignment(Align.center);
        root.add(clockLabel).center().pad(10).width(150); // Increased width for "Day X, HH:MM"
        root.row();

        // Speed control buttons
        VisTable buttonTable = new VisTable();
        buttonTable.defaults().pad(5).width(60);

        VisTextButton pauseButton = new VisTextButton("PAUSE");
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameState.togglePause();
            }
        });

        VisTextButton normalButton = new VisTextButton("1x");
        normalButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameState.setTimeScale(1.0f);
            }
        });

        VisTextButton fastButton = new VisTextButton("3x");
        fastButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameState.setTimeScale(3.0f);
            }
        });

        VisTextButton fasterButton = new VisTextButton("5x");
        fasterButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameState.setTimeScale(5.0f);
            }
        });

        buttonTable.add(pauseButton);
        buttonTable.add(normalButton);
        buttonTable.add(fastButton);
        buttonTable.add(fasterButton);

        root.add(buttonTable).center();
        root.row();

        pack();
        updatePosition();
    }

    private void updatePosition() {
        setPosition(
            (Gdx.graphics.getWidth() - getWidth()) / 2,
            Gdx.graphics.getHeight() - getHeight() - (10 / UI_SCALE)
        );
    }

    public void update() {
        // Now displays "Day X, HH:00" format since we updated getTimeString() in GameClock
        clockLabel.setText(GameClock.getTimeString());
    }

    @Override
    public void resize() {
        updatePosition();
    }

    @Override
    public VisWindow get() {
        return this;
    }
}
