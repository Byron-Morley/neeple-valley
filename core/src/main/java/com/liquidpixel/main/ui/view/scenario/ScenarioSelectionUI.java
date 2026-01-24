package com.liquidpixel.main.ui.view.scenario;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.*;
import com.liquidpixel.main.interfaces.IGet;
import com.liquidpixel.main.interfaces.services.IScenarioService;
import com.liquidpixel.main.interfaces.ui.IResizePosition;
import com.liquidpixel.main.ui.common.ReuseableWindow;

import java.util.List;

public class ScenarioSelectionUI extends ReuseableWindow implements IGet<Group>, IResizePosition {

    private final IScenarioService scenarioService;
    private VisTable scenarioList;
    private VisTextButton playButton;
    private VisTextButton resetButton;
    private VisLabel currentScenarioLabel;
    private String selectedScenario;
    private VisList<String> list;

    public ScenarioSelectionUI(IScenarioService scenarioService) {
        super("Scenario Tester");
        this.scenarioService = scenarioService;
        setupUI();
        refreshScenarioList();
    }

    private void setupUI() {
        // Scenario list
        scenarioList = new VisTable();
        list = new VisList<>();
        VisScrollPane scrollPane = new VisScrollPane(list);
//        VisScrollPane scrollPane = new VisScrollPane(scenarioList);
        scrollPane.setFadeScrollBars(false);
        add(scrollPane).width(300).height(200).padBottom(20).padRight(0).row();

        // Current scenario label
        currentScenarioLabel = new VisLabel("No scenario selected");
        add(currentScenarioLabel).padBottom(20).row();

        // Control buttons
        VisTable buttonTable = new VisTable();

        playButton = new VisTextButton("Play");
        playButton.setDisabled(true);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedScenario != null) {
                    scenarioService.loadScenario(selectedScenario);
                    scenarioService.startCurrentScenario();
                    updateButtonStates();
                }
            }
        });

        resetButton = new VisTextButton("Reset");
        resetButton.setDisabled(true);
        resetButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (scenarioService.hasCurrentScenario()) {
                    scenarioService.resetCurrentScenario();
                }
            }
        });

        buttonTable.add(playButton).padRight(10);
        buttonTable.add(resetButton);
        add(buttonTable).row();

        pack();
    }

    private void refreshScenarioList() {
        scenarioList.clear();

        List<String> scenarios = scenarioService.getAvailableScenarios();
        Array<String> items = new Array<>();

        list.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectScenario(list.getSelected());
            }
        });

        for (String scenario : scenarios) {
            items.add(scenario);
        }

        list.setItems(items);
    }

    private void selectScenario(String scenario) {
        selectedScenario = scenario;
        currentScenarioLabel.setText("Selected: " + scenario);
        updateButtonStates();
    }

    private void updateButtonStates() {
        boolean hasSelection = selectedScenario != null;
        boolean hasCurrentScenario = scenarioService.hasCurrentScenario();

        playButton.setDisabled(!hasSelection);
        resetButton.setDisabled(!hasCurrentScenario);
    }

    @Override
    public void resize() {
        if (getStage() != null) {
            float rightX = getStage().getWidth() - getWidth() - 20; // 20px margin from right edge
            float topY = getStage().getHeight() - getHeight() - 50; // 50px margin from top
            setPosition(rightX, topY);
        }
    }

    @Override
    public Group get() {
        return this;
    }
}
