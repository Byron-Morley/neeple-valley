package com.liquidpixel.main.ui.view.debug.topRight;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import java.text.DecimalFormat;

class DebugLabel extends VisLabel {
    public DebugLabel(String text) {
        super(text);
        this.setFontScale(1.5f);
    }
}

public class DebugUI extends VisTable {

    private Label fpsLabel;
    private Label positionLabel;
    private Label labelThree;
    private Label labelFour;
    private Label labelFive;
    private Label labelSix;
    private Label labelSeven;
    DecimalFormat df;

    public DebugUI() {
        super();

        this.fpsLabel = new DebugLabel("FPS: unknown");
        this.positionLabel = new DebugLabel("Current Position: unknown");
        this.labelThree = new DebugLabel("");
        this.labelFour = new DebugLabel("");
        this.labelFive = new DebugLabel("");
        this.labelSix = new DebugLabel("");
        this.labelSeven = new DebugLabel("");

        df = new DecimalFormat("#.##");

        pad(5);
        align(Align.topRight);
        add(fpsLabel).right();
        row();
        add(positionLabel).right();
        row();
        add(labelThree).right();
        row();
        add(labelFour).right();
        row();
        add(labelFive).right();
        row();
        add(labelSix).right();
        row();
        add(labelSeven).right();
        row();

    }

    public void setFPS(int fps) {
        this.fpsLabel.setText("FPS: " + fps);
    }

    public void setPositionLabel(GridPoint2 vector) {
        this.positionLabel.setText("Current Position: (" + df.format(vector.x) + " , " + df.format(vector.y) + ")");
    }

    public void setLabelThree(String text) {
        this.labelThree.setText(text);
    }

    public void setLabelFour(String text) {
        this.labelFour.setText(text);
    }

    public void setLabelFive(String text) {
        this.labelFive.setText(text);
    }

    public void setLabelSix(String text) {
        this.labelSix.setText(text);
    }

    public void setLabelSeven(String text) {
        this.labelSeven.setText(text);
    }
}
