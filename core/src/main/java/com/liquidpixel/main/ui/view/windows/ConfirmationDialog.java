package com.liquidpixel.main.ui.view.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.liquidpixel.main.ui.common.ReuseableWindow;
import com.liquidpixel.main.interfaces.IGet;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class ConfirmationDialog extends ReuseableWindow implements IGet<Group> {

    private final VisLabel messageLabel;
    private final VisTextButton confirmButton;
    private final VisTextButton cancelButton;
    private final Table buttonTable;

    private Runnable confirmAction;
    private Runnable cancelAction;

    public ConfirmationDialog() {
        super("Confirmation");

        // Make the dialog modal and non-movable
        setMovable(false);
        setVisible(false);
        setModal(true);

        // Create components
        messageLabel = new VisLabel("Are you sure?");
        messageLabel.setWrap(true);

        confirmButton = new VisTextButton("Yes");
        cancelButton = new VisTextButton("No");

        // Create button table
        buttonTable = new Table();
        buttonTable.add(cancelButton).padRight(10).expandX().fillX();
        buttonTable.add(confirmButton).expandX().fillX();

        // Add listeners
        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                handleConfirm();
            }
        });

        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                handleCancel();
            }
        });

        // Layout the dialog
        setupLayout();
    }

    private void setupLayout() {
        // Clear existing content
        clearChildren();

        // Create main table
        Table mainTable = new Table();
        mainTable.pad(20);

        // Add message with word wrap
        mainTable.add(messageLabel).width(300).padBottom(20).row();

        // Add buttons
        mainTable.add(buttonTable).width(300).height(40);

        // Add to window
        add(mainTable);
        pack();
    }

    public void show(String message, Runnable confirmAction) {
        show(message, confirmAction, null);
    }

    public void show(String message, Runnable confirmAction, Runnable cancelAction) {
        this.confirmAction = confirmAction;
        this.cancelAction = cancelAction;

        // Set the message
        messageLabel.setText(message);

        // Center the dialog on screen
        centerOnScreen();

        // Show the dialog
        setVisible(true);

        // Focus on the cancel button by default (safer option)
        getStage().setKeyboardFocus(cancelButton);
    }

    public void show(String message, String confirmText, String cancelText, Runnable confirmAction) {
        show(message, confirmText, cancelText, confirmAction, null);
    }

    public void show(String message, String confirmText, String cancelText, Runnable confirmAction, Runnable cancelAction) {
        // Update button texts
        confirmButton.setText(confirmText);
        cancelButton.setText(cancelText);

        // Repack to accommodate new text sizes
        setupLayout();

        // Show with custom actions
        show(message, confirmAction, cancelAction);
    }

    private void handleConfirm() {
        if (confirmAction != null) {
            confirmAction.run();
        }
        hide();
    }

    private void handleCancel() {
        if (cancelAction != null) {
            cancelAction.run();
        }
        hide();
    }

    public void hide() {
        setVisible(false);
        confirmAction = null;
        cancelAction = null;
    }

    private void centerOnScreen() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float windowWidth = getWidth();
        float windowHeight = getHeight();

        float x = (screenWidth - windowWidth) / 2;
        float y = (screenHeight - windowHeight) / 2;

        setPosition(x, y);
    }

    @Override
    public Group get() {
        return this;
    }
}
