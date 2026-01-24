package com.liquidpixel.main.ui.view.selection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.interfaces.services.ICameraService;
import com.liquidpixel.main.interfaces.IGet;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

public class SelectionUI extends VisTable implements IGet<Group> {

    ISelectionService selectionService;
    ICameraService cameraService;
    Stage stage;
    VisLabel label;
    VisTable labelTable;
    private static final float LABEL_HEIGHT_PERCENTAGE = 0.25f; // 25% of the tile height

    public SelectionUI(
        ISelectionService selectionService,
        ICameraService cameraService
    ) {
        this.selectionService = selectionService;
        this.cameraService = cameraService;
        this.stage = GameResources.get().getStage();
        init();
    }

    public void init() {
        setVisible(true);

        // Make this UI ignore all touch/click events
        setTouchable(Touchable.disabled);

        // Don't use the table layout system for the label
        // We'll position it manually
        this.setFillParent(false);

        // Create label table with opaque background
        labelTable = new VisTable();
//    labelTable.setBackground(com.kotcrab.vis.ui.VisUI.getSkin().getDrawable("window"));
//    labelTable.setColor(new Color(0.2f, 0.2f, 0.2f, 0.8f));

        // Create the label
        label = new VisLabel("");
        label.setAlignment(Align.center);

        // Add label to label table
        labelTable.add(label).expandX().fillX().center();

        // Add label table as a direct child (not using table layout)
        this.addActor(labelTable);

        // Also make the label table non-interactive
        labelTable.setTouchable(Touchable.disabled);
    }


    public void updateUIElements(Vector2 entityCoordinates, float entityWidth, float entityHeight, String labelString) {
        // Create copies of the vectors to avoid modifying the originals
        Vector2 lowerLeft = new Vector2(entityCoordinates);
        Vector2 upperLeft = new Vector2(entityCoordinates.x, entityCoordinates.y + entityHeight);
        Vector2 lowerRight = new Vector2(entityCoordinates.x + entityWidth, entityCoordinates.y);

        // Project all points to screen coordinates
        cameraService.project(lowerLeft);
        cameraService.project(upperLeft);
        cameraService.project(lowerRight);

        // Calculate width and height in screen coordinates
        float width = lowerRight.x - lowerLeft.x;
        float height = upperLeft.y - lowerLeft.y;

        // Set dimensions for the main selection area
        this.setWidth(width);
        this.setHeight(height);
        this.setPosition(lowerLeft.x, lowerLeft.y);

        // Use a fixed pixel height for the label that doesn't change with zoom
        float fixedLabelHeight = 20; // Adjust this value to your preference

        // Update the label
        label.setText(labelString);
        label.setFontScale(width / 40);

        // Update the label table with fixed height
        labelTable.setHeight(fixedLabelHeight);
        labelTable.setWidth(width);
        labelTable.setPosition(0, 0);

        // Ensure the label table is visible when there's text
        labelTable.setVisible(!labelString.isEmpty());

        // Force the layout to update
        this.invalidate();
        this.validate();
    }


    @Override
    public Table get() {
        return this;
    }
}
