package com.liquidpixel.main.ui.view.infoPanel.mocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class WindowListItemUI extends VisTable {
    private final String itemName;

    public WindowListItemUI(String itemName) {
        this.itemName = itemName;
        setupUI();
    }

    private void setupUI() {
        // Set background for visibility
        setBackground("window-bg");

        // Create item name label
        VisLabel nameLabel = new VisLabel(itemName);
        nameLabel.setFontScale(1.1f);

        // Create quantity label (mock data)
        VisLabel quantityLabel = new VisLabel("Qty: " + (int)(Math.random() * 50 + 1));
        quantityLabel.setColor(Color.GRAY);

        // Create action button
        VisTextButton actionButton = new VisTextButton("Use");
        actionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Using item: " + itemName);
            }
        });

        // Layout: Name | Quantity | Button
        add(nameLabel).expandX().left().padLeft(10);
        add(quantityLabel).padRight(10);
        add(actionButton).width(60).padRight(10);

        // Add padding around the entire item
        pad(5);
    }
}
