package com.liquidpixel.main.ui.view.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;

public class MenuItemUI extends VisTable {
    private static final Color LABEL_BACKGROUND = new Color(0, 0, 0, 0.8f);
    private static final float ICON_SIZE = 60f;
    private static final float LABEL_HEIGHT = 20f;
    private boolean selected = false;
    private boolean hovered = false;
    private final VisImage iconImage;
    private TextureRegion normalTexture;
    private TextureRegion selectedTexture;
    String itemName;
    String label;

    public MenuItemUI(ISpriteFactory spriteFactory, String label, String iconName, String itemName) {
        this.itemName = itemName;
        this.label = label;
        VisTable labelPanel = new VisTable();
        labelPanel.setBackground("white");
        labelPanel.setColor(LABEL_BACKGROUND);

        VisLabel visLabel = new VisLabel(label, "small");
        visLabel.setAlignment(Align.center);
        labelPanel.add(visLabel).width(ICON_SIZE).center();

        VisTable iconPanel = new VisTable();

        try {
            normalTexture = spriteFactory.getTextureWithFallback(iconName, -1);
            if (normalTexture == null) {
                System.err.println("Failed to load texture for icon: " + iconName);
            }

            try {
                selectedTexture = spriteFactory.getTextureWithFallback(iconName + "-selected", -1);
            } catch (Exception ignored) {
            }

            if (selectedTexture == null) {
                selectedTexture = normalTexture;
            }
        } catch (Exception e) {
            System.err.println("Error loading texture for icon: " + iconName);
            e.printStackTrace();
        }
        iconImage = new VisImage(normalTexture);
        iconPanel.add(iconImage).size(ICON_SIZE);

//        add(labelPanel).width(ICON_SIZE).height(LABEL_HEIGHT);
        row();
        add(iconPanel).size(ICON_SIZE);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        updateIcon();
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
        updateIcon();
    }

    private void updateIcon() {
        if (selected || hovered) {
            if (selectedTexture != null) iconImage.setDrawable(new TextureRegionDrawable(selectedTexture));
        } else {
            if (normalTexture != null) iconImage.setDrawable(new TextureRegionDrawable(normalTexture));
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public String getItemName() {
        return itemName;
    }

    public String getLabel() {
        return label;
    }
}
