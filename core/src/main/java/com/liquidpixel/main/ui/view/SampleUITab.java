package com.liquidpixel.main.ui.view;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.liquidpixel.main.ui.UiTestScreen;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisWindow;

public class SampleUITab implements UiTestScreen.TestUITab {

    @Override
    public String getTabTitle() {
        return "Sample UI";
    }

    @Override
    public void setupUI(VisTable contentTable) {
        // Create a section for basic controls
        VisTable controlsSection = new VisTable();
        controlsSection.defaults().padRight(10).padBottom(5);

        controlsSection.add(new VisLabel("Basic Controls")).colspan(2).padBottom(10).row();

        // Add a text field
        controlsSection.add(new VisLabel("Text Input:"));
        VisTextField textField = new VisTextField("Edit me");
        controlsSection.add(textField).width(200).row();

        // Add a checkbox
        controlsSection.add(new VisLabel("Checkbox:"));
        VisCheckBox checkBox = new VisCheckBox("Enable Feature");
        controlsSection.add(checkBox).row();

        // Add a slider
        controlsSection.add(new VisLabel("Slider:"));
        VisSlider slider = new VisSlider(0, 100, 1, false);
        controlsSection.add(slider).width(200).row();

        // Add a button
        controlsSection.add(new VisLabel("Button:"));
        VisTextButton button = new VisTextButton("Click Me");
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Create a popup window when button is clicked
                createPopupWindow();
            }
        });
        controlsSection.add(button).row();

        // Add a section for layout demonstration
        VisTable layoutSection = new VisTable();
        layoutSection.defaults().pad(5);

        layoutSection.add(new VisLabel("Layout Example")).colspan(3).padBottom(10).row();

        // Create a 3x3 grid of buttons
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                VisTextButton gridButton = new VisTextButton("Btn " + (i*3+j+1));
                layoutSection.add(gridButton).width(80).height(40);
            }
            layoutSection.row();
        }

        // Add sections to content table
        contentTable.add(controlsSection).pad(20).top().left().row();
        contentTable.add(layoutSection).pad(20).top().left().row();
        contentTable.add().expand().row(); // Push everything to the top
    }

    private void createPopupWindow() {
        VisWindow window = new VisWindow("Popup Window");
        window.add(new VisLabel("This is a sample popup window")).pad(10).row();

        VisTextButton closeButton = new VisTextButton("Close");
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                window.fadeOut();
            }
        });

        window.add(closeButton).padBottom(10);
        window.pack();
        window.centerWindow();
        window.fadeIn();
    }
}
