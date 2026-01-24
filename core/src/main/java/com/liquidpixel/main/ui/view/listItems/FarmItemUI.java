package com.liquidpixel.main.ui.view.listItems;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.liquidpixel.main.components.items.FarmComponent;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.sprite.model.GameSprite;
import com.liquidpixel.main.ui.components.ProgressCircle;
import com.liquidpixel.main.ui.model.farming.FarmItem;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class FarmItemUI extends ListItemUI {

    FarmComponent farmComponent;
    ClickListener removeCropButtonListener;
    FarmItem item;


    public FarmItemUI(FarmItem item, FarmComponent farmComponent, ClickListener removeCropButtonListener) {
        super();
        this.farmComponent = farmComponent;
        this.removeCropButtonListener = removeCropButtonListener;
        addTableRow(item);
    }

    private void addTableRow(FarmItem item) {
        this.item = item;
        setBackground("window-bg");
        pad(10);

        // Top section with icon buttons on the right
        VisTable topSection = new VisTable();
        topSection.add().expandX(); // Empty space to push buttons to the right

        // Create 3 icon buttons using placeholder sprites
        VisImageButton iconButton1 = createIconButton("missing", "Icon 1");
        VisImageButton iconButton2 = createIconButton("missing", "Icon 2");
        VisImageButton removeButton = createIconButton("missing", "Remove");


        removeButton.addListener(removeCropButtonListener);

//        topSection.add(iconButton1).size(24, 24).padRight(5);
//        topSection.add(iconButton2).size(24, 24).padRight(5);
        topSection.add(removeButton).size(24, 24);

        add(topSection).growX().top().row();

        // Main content section: Image | Content | Time
        VisTable mainSection = new VisTable();

        // Left side: Item image
        Image itemImage = createItemImage(item);
        mainSection.add(itemImage).width(32).height(32).padRight(15).center();

        // Middle: Content area with name and quantity
        VisTable contentArea = new VisTable();
        VisLabel nameLabel = new VisLabel(item.getName());
        nameLabel.setFontScale(1.1f);
        nameLabel.setColor(com.badlogic.gdx.graphics.Color.WHITE);

        VisLabel quantityLabel = new VisLabel("Quantity: x" + item.getQuantity());
        quantityLabel.setColor(com.badlogic.gdx.graphics.Color.LIGHT_GRAY);

        contentArea.add(nameLabel).expandX().left().row();
        contentArea.add(quantityLabel).expandX().left().row();

        mainSection.add(contentArea).expandX().fillX().padRight(15).center();

        // Right side: Time information and plant button
        VisTable timeArea = new VisTable();

        // Create a sub-table for the time labels
        VisTable timeLabels = new VisTable();
        VisLabel daysLabel = new VisLabel(item.getDays() + " days");
        VisLabel hoursLabel = new VisLabel(item.getHours() + " hours");
        daysLabel.setColor(com.badlogic.gdx.graphics.Color.CYAN);
        hoursLabel.setColor(com.badlogic.gdx.graphics.Color.CYAN);

        timeLabels.add(daysLabel).right().row();
        timeLabels.add(hoursLabel).right().row();

        // Add time labels to the left side of timeArea
        timeArea.add(timeLabels).right().padRight(10);

        // Add plant button or progress circle to the right of time labels
        if (item.isPlanted()) {
            ProgressCircle progressCircle = new ProgressCircle(15f);
            progressCircle.setProgress(item.getGrowthProgress());
            timeArea.add(progressCircle).center();
        } else {
            VisTextButton plantButton = new VisTextButton("Plant");
            plantButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    item.setPlanted(true);
                    farmComponent.setPlanted(true);
                    refreshDisplay(item);
                }
            });
            timeArea.add(plantButton).center();
        }

        mainSection.add(timeArea).right().center();

        // Add main section to the table
        add(mainSection).growX().center().row();

        // Add bottom padding that matches the height of the top icon bar
        VisTable bottomPadding = new VisTable();
        bottomPadding.setHeight(24); // Same height as icon buttons
        add(bottomPadding).growX().height(24);
    }

    private VisImageButton createIconButton(String spriteName, String tooltip) {
        try {
//            GameSprite sprite = SpriteFactory.getSprite(spriteName);
            GameSprite sprite = null;
            VisImageButton button = new VisImageButton(new TextureRegionDrawable(sprite));

            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Clicked: " + tooltip);
                }
            });

            return button;
        } catch (Exception e) {
            // Fallback: create a simple drawable and button if sprite not found
            try {
                // Try to get any available sprite as fallback
//                GameSprite fallbackSprite = SpriteFactory.getSprite("missing");
                GameSprite fallbackSprite = null;
                VisImageButton button = new VisImageButton(new TextureRegionDrawable(fallbackSprite));

                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        System.out.println("Clicked: " + tooltip + " (fallback)");
                    }
                });

                return button;
            } catch (Exception fallbackException) {
                // If even fallback fails, create an empty image drawable
                TextureRegion emptyRegion = new TextureRegion();
                VisImageButton button = new VisImageButton(new TextureRegionDrawable(emptyRegion));

                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        System.out.println("Clicked: " + tooltip + " (empty)");
                    }
                });

                return button;
            }
        }
    }

    private void refreshDisplay(FarmItem item) {
        // Clear the current table and rebuild it
        clear();
        addTableRow(item);
    }

    private Image createItemImage(IStorageItem item) {
        try {
            // Use the sprite from the IStorageItem
            GameSprite sprite = item.getSprite();

            int spriteWidth = sprite.getRegionWidth();
            int spriteHeight = sprite.getRegionHeight();

            // Check if sprite is square - if not, crop to bottom square portion
            if (spriteWidth != spriteHeight && spriteHeight > spriteWidth) {
                // For non-square sprites (like 16x32), crop to bottom 16x16 portion
                TextureRegion croppedRegion = new TextureRegion(
                    sprite.getTexture(),
                    sprite.getRegionX(),
                    sprite.getRegionY() + (spriteHeight - spriteWidth), // Start at bottom square portion
                    spriteWidth, // Width: same as original width
                    spriteWidth  // Height: make it square (same as width)
                );

                Image image = new Image(new TextureRegionDrawable(croppedRegion));
                image.setScaling(com.badlogic.gdx.utils.Scaling.stretch);
                return image;
            } else {
                // For square sprites, use the original sprite
                Image image = new Image(sprite);
                image.setScaling(com.badlogic.gdx.utils.Scaling.fit);
                return image;
            }
        } catch (Exception e) {
            // Fallback to a default texture if item sprite not found
            System.out.println("Could not load sprite for: " + item.getName());
            try {
//                GameSprite fallbackSprite = SpriteFactory.getSprite("missing");
                GameSprite fallbackSprite = null;
                Image fallbackImage = new Image(fallbackSprite);
                fallbackImage.setScaling(com.badlogic.gdx.utils.Scaling.fit); // Maintain aspect ratio
                return fallbackImage;
            } catch (Exception fallbackException) {
                // Return empty image if no fallback available
                return new Image();
            }
        }
    }

    public FarmItem getItem() {
        return item;
    }
}
