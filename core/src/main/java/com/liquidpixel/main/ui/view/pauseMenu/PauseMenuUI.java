package com.liquidpixel.main.ui.view.pauseMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.liquidpixel.main.engine.GameState;
import com.liquidpixel.main.interfaces.IGet;
import com.liquidpixel.main.interfaces.managers.ILoadAndSaveManager;
import com.liquidpixel.main.interfaces.ui.IResizePosition;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

public class PauseMenuUI extends VisWindow implements IGet<Group>, IResizePosition {

    public PauseMenuUI(ILoadAndSaveManager loadAndSaveManager) {
        super("Pause");
        setVisible(false);
        setMovable(false);

        VisTextButton continueButton = new VisTextButton("Continue");
        VisTextButton restartButton = new VisTextButton("Restart");
        VisTextButton loadButton = new VisTextButton("Load");
        VisTextButton saveButton = new VisTextButton("Save");
        VisTextButton saveAsButton = new VisTextButton("Save As");
        VisTextButton exitButton = new VisTextButton("Exit");

        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setVisible(false);
                GameState.setPaused(false);
            }
        });

        //RESTART
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                loadAndSaveManager.newGame(() -> {
                    System.out.println("Game Loaded!");
                    GameState.setPaused(true);
                });
            }
        });


        //LOAD
        loadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                loadAndSaveManager.loadGame("mysave", () -> {
                    System.out.println("Game Loaded!");
                    GameState.setPaused(true);
                });
            }
        });

        //SAVE
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                continueButton.setDisabled(true);
                saveButton.setDisabled(true);
                saveAsButton.setDisabled(true);
                exitButton.setDisabled(true);
                loadButton.setDisabled(true);

                loadAndSaveManager.saveGame("mysave", () -> {
                    System.out.println("Save completed!");

                    continueButton.setDisabled(false);
                    saveButton.setDisabled(false);
                    saveAsButton.setDisabled(false);
                    exitButton.setDisabled(false);
                    loadButton.setDisabled(false);

                });
            }
        });

        //SAVE AS
        saveAsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });


        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        float buttonWidth = 200f;
        float buttonHeight = 40f;


        add(continueButton).width(buttonWidth).height(buttonHeight).pad(5).row();
        add(restartButton).width(buttonWidth).height(buttonHeight).pad(5).row();
        add(loadButton).width(buttonWidth).height(buttonHeight).pad(5).row();
        add(saveButton).width(buttonWidth).height(buttonHeight).pad(5).row();
        add(saveAsButton).width(buttonWidth).height(buttonHeight).pad(5).row();
        add(exitButton).width(buttonWidth).height(buttonHeight).pad(5).row();

        pack();

        setPosition(
            (Gdx.graphics.getWidth() - getWidth()) / 2,
            (Gdx.graphics.getHeight() - getHeight()) / 2
        );
    }

    @Override
    public VisWindow get() {
        return this;
    }

    @Override
    public void resize() {
        setPosition(
            (Gdx.graphics.getWidth() - getWidth()) / 2,
            (Gdx.graphics.getHeight() - getHeight()) / 2
        );
    }
}
