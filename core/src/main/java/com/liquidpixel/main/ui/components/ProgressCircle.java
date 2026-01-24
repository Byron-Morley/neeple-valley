package com.liquidpixel.main.ui.components;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

public class ProgressCircle extends VisTable {

    private VisLabel progressLabel;
    private float progress; // 0.0 to 1.0

    public ProgressCircle(float radius) {
        this.progress = 0.0f;

        // Create a circular progress indicator using text
        progressLabel = new VisLabel("●");
        progressLabel.setFontScale(1.5f);
        progressLabel.setColor(Color.DARK_GRAY);

        // Set widget size
        setSize(radius * 2, radius * 2);

        // Add the label to the center of the table
        add(progressLabel).center();

        updateDisplay();
    }

    private void updateDisplay() {
        // Create a visual representation of progress using Unicode characters
        if (progress >= 1.0f) {
            progressLabel.setText("●"); // Full circle
            progressLabel.setColor(Color.GREEN);
        } else if (progress >= 0.75f) {
            progressLabel.setText("◐"); // 3/4 circle
            progressLabel.setColor(Color.YELLOW);
        } else if (progress >= 0.5f) {
            progressLabel.setText("◑"); // 1/2 circle
            progressLabel.setColor(Color.ORANGE);
        } else if (progress >= 0.25f) {
            progressLabel.setText("◒"); // 1/4 circle
            progressLabel.setColor(Color.RED);
        } else if (progress > 0.0f) {
            progressLabel.setText("○"); // Empty circle with slight progress
            progressLabel.setColor(Color.LIGHT_GRAY);
        } else {
            progressLabel.setText("○"); // Empty circle
            progressLabel.setColor(Color.DARK_GRAY);
        }
    }

    public void setProgress(float progress) {
        this.progress = Math.max(0.0f, Math.min(1.0f, progress));
        updateDisplay();
    }

    public float getProgress() {
        return progress;
    }
}
