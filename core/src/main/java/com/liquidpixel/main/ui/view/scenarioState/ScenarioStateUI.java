package com.liquidpixel.main.ui.view.scenarioState;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.*;
import com.liquidpixel.main.interfaces.IGet;
import com.liquidpixel.main.interfaces.IScenario;
import com.liquidpixel.main.interfaces.ScenarioState;
import com.liquidpixel.main.interfaces.Updatable;
import com.liquidpixel.main.interfaces.services.IScenarioService;
import com.liquidpixel.main.interfaces.ui.IResizePosition;
import com.liquidpixel.main.ui.common.ReuseableWindow;
import com.liquidpixel.main.ui.view.common.ScrollPanelUI;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.liquidpixel.main.screens.WorldScreen.UI_SCALE;

public class ScenarioStateUI extends ReuseableWindow implements IGet<Group>, Updatable, IResizePosition {

    IScenarioService scenarioService;
    private final VisTable contentTable;
    private IScenario lastScenario = null;
    private Tree tree;

    class Node extends Tree.Node<Node, String, VisTextButton> {
        public Node(String text, ClickListener clickListener) {
            super(new VisTextButton(text, "menu"));
            setValue(text);
            this.getActor().addListener(clickListener);
        }
    }

    class MenuNode extends Tree.Node<Node, String, VisTextButton> {
        public MenuNode(String text) {
            super(new VisTextButton(text, "menu"));
            setValue(text);
        }
    }

    public ScenarioStateUI(IScenarioService scenarioService) {
        super("Scenario States");
        this.scenarioService = scenarioService;
        setVisible(false);
        addCloseButton();

        // Create content table directly in the window
        contentTable = new VisTable();
        contentTable.top();

        defaults().pad(10, 5, 10, 5);
        row().fill().expandX().expandY();
        add(contentTable).width(300 / UI_SCALE).height(400 / UI_SCALE);
        pack();
        updateContent();
    }

    public void updateContent() {
        // Clear existing content
        contentTable.clear();

        // Add header
        VisLabel headerLabel = new VisLabel("Scenario States");
        headerLabel.setFontScale(1.2f);
        contentTable.add(headerLabel).center().padBottom(10).row();

        // Check if a scenario is currently loaded
        if (scenarioService.getCurrentScenario() == null) {
            VisLabel noScenarioLabel = new VisLabel("No scenario loaded");
            noScenarioLabel.setColor(0.8f, 0.8f, 0.8f, 1f);
            contentTable.add(noScenarioLabel).center().pad(20).row();
            return;
        }

        // Add scenario name
        IScenario currentScenario = scenarioService.getCurrentScenario();
        String scenarioName = currentScenario.getClass().getSimpleName();
        VisLabel scenarioLabel = new VisLabel("Current: " + scenarioName);
        contentTable.add(scenarioLabel).left().padBottom(15).row();

        // Get available states from the scenario
        List<ScenarioState> availableStates = currentScenario.getAvailableStates();

        if (availableStates.isEmpty()) {
            // No custom states available - add generic reset button
            addGenericResetButton();
        } else {
            // Add tree for available states
            tree = new VisTree();
            tree.setPadding(10);
            tree.setIndentSpacing(10);
            tree.setIconSpacing(5, 0);

            buildStateTree(availableStates);

            ScrollPane scrollPane = new ScrollPanelUI(tree);
            Table listPanel = new VisTable();
            listPanel.add(scrollPane).grow().padBottom(12).padRight(0);

            contentTable.add(listPanel).growX().growY();
        }

        pack();
    }

    private void buildStateTree(List<ScenarioState> availableStates) {
        Map<String, List<ScenarioState>> groupedStates = new LinkedHashMap<>();

        // Group states by menu category
        for (ScenarioState state : availableStates) {
            String displayName = state.getDisplayName();
            String[] parts = displayName.split(" ");
            String menu = parts[0];

            if (!groupedStates.containsKey(menu)) {
                groupedStates.put(menu, new ArrayList<>());
            }
            groupedStates.get(menu).add(state);
        }

        // Build tree
        for (Map.Entry<String, List<ScenarioState>> entry : groupedStates.entrySet()) {
            String menuName = entry.getKey();
            List<ScenarioState> states = entry.getValue();

            if (states.size() == 1) {
                // Single item - add as direct node
                ScenarioState state = states.get(0);
                tree.add(createNode(state.getDisplayName(), state));
            } else {
                // Multiple items - add dropdown
                MenuNode menuNode = new MenuNode(menuName);
                tree.add(menuNode);

                for (ScenarioState state : states) {
                    String displayName = state.getDisplayName();
                    String nodeText = displayName.startsWith(menuName + " ")
                            ? displayName.substring(menuName.length() + 1)
                            : displayName;
                    menuNode.add(createNode(nodeText, state));
                }
            }
        }
    }

    private Node createNode(String text, ScenarioState state) {
        return new Node(text, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clicked on " + state.getDisplayName());
                loadScenarioState(state.getId());
            }
        });
    }

    private void addGenericResetButton() {
        VisTextButton resetButton = new VisTextButton("Reset Scenario");
        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (scenarioService.hasCurrentScenario()) {
                    scenarioService.resetCurrentScenario();
                }
            }
        });
        contentTable.add(resetButton).growX().height(40).padBottom(5).row();

        // Add info label
        VisLabel infoLabel = new VisLabel("No custom states available\nfor this scenario type");
        infoLabel.setColor(0.8f, 0.8f, 0.8f, 1f);
        contentTable.add(infoLabel).center().pad(20).row();
    }

    private void loadScenarioState(String stateId) {
        if (scenarioService.getCurrentScenario() != null) {
            IScenario currentScenario = scenarioService.getCurrentScenario();
            boolean success = currentScenario.loadState(stateId);

            if (success) {
                System.out.println("Successfully loaded state: " + stateId);
            } else {
                System.out.println("Failed to load state: " + stateId);
                // Fallback to reset if state loading failed
                currentScenario.reset();
            }
        }
    }

    @Override
    public void resize() {
        if (getStage() != null) {
            float rightX = getStage().getWidth() - getWidth() - 20; // 20px margin from right edge
            float bottomY = 20; // 20px margin from bottom edge
            setPosition(rightX, bottomY);
        }
    }

    @Override
    public VisWindow get() {
        return this;
    }

    @Override
    public void update() {
        // Only update content when scenario changes
        IScenario currentScenario = scenarioService.getCurrentScenario();
        if (currentScenario != lastScenario) {
            lastScenario = currentScenario;
            updateContent();
        }
    }
}
