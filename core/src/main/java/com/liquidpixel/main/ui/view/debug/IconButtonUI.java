package com.liquidpixel.main.ui.view.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.liquidpixel.main.interfaces.IGet;
import com.liquidpixel.main.interfaces.ui.IResizePosition;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;

import static com.liquidpixel.main.screens.WorldScreen.UI_SCALE;

public class IconButtonUI extends VisTable implements IGet<Group>, IResizePosition {

    private VisImageButton iconButton;
    private VisTextButton fallbackButton;
    private static final int BUTTON_SIZE = 48;
    private static final int MARGIN = 20;

    // Toggle state for color change
    private boolean isSelected = false;
    private static final Color NORMAL_COLOR = Color.WHITE;
    private static final Color SELECTED_COLOR = Color.GREEN;
    ISpriteFactory spriteFactory;


    public IconButtonUI(ISpriteFactory spriteFactory) {
        this.spriteFactory = spriteFactory;
        init();
    }

    private void init() {
        setVisible(false);
        setFillParent(false);

        // Try to create an image button with an actual icon
        boolean imageButtonCreated = false;

        try {
            // Try to load the select icon from the atlas
            TextureRegion iconTexture = spriteFactory.getTextureWithFallback("select", -1);

            if (iconTexture != null) {
                // Create image button with the loaded texture as drawable
                TextureRegionDrawable iconDrawable = new TextureRegionDrawable(iconTexture);
                iconButton = new VisImageButton(iconDrawable);
                iconButton.setSize(BUTTON_SIZE / UI_SCALE, BUTTON_SIZE / UI_SCALE);

                iconButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        onIconButtonClicked();
                    }

                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        iconButton.getImage().setColor(Color.YELLOW);
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        iconButton.getImage().setColor(isSelected ? SELECTED_COLOR : NORMAL_COLOR);
                    }
                });

                add(iconButton).size(BUTTON_SIZE / UI_SCALE, BUTTON_SIZE / UI_SCALE);
                imageButtonCreated = true;
                Gdx.app.log("IconButtonUI", "Successfully created image button with select icon");
            }
        } catch (Exception e) {
            Gdx.app.log("IconButtonUI", "Failed to create image button: " + e.getMessage());
        }

        // Fallback to text button if image button failed
        if (!imageButtonCreated) {
            fallbackButton = new VisTextButton("🏠");
            fallbackButton.setSize(BUTTON_SIZE / UI_SCALE, BUTTON_SIZE / UI_SCALE);

            fallbackButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    onIconButtonClicked();
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    fallbackButton.setColor(Color.YELLOW);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    fallbackButton.setColor(isSelected ? SELECTED_COLOR : NORMAL_COLOR);
                }
            });

            add(fallbackButton).size(BUTTON_SIZE / UI_SCALE, BUTTON_SIZE / UI_SCALE);
            Gdx.app.log("IconButtonUI", "Using fallback text button with house emoji");
        }

        pack();
        updatePosition();
    }

    private void onIconButtonClicked() {
        // Toggle the selection state
        isSelected = !isSelected;

        Gdx.app.log("IconButtonUI", "Select button clicked! Selected: " + isSelected);
    }

    private void updatePosition() {
        // Position in bottom right corner with margin
        setPosition(
            Gdx.graphics.getWidth() - getWidth() - (MARGIN / UI_SCALE),
            MARGIN / UI_SCALE
        );
    }

    @Override
    public void resize() {
        updatePosition();
    }

    @Override
    public VisTable get() {
        return this;
    }
}
