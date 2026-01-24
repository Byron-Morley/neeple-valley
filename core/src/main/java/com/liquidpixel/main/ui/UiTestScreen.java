package com.liquidpixel.main.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.liquidpixel.main.interfaces.Updatable;
import com.liquidpixel.main.ui.view.SampleUITab;
import com.liquidpixel.main.ui.view.infoPanel.mocks.*;
import com.liquidpixel.main.ui.view.infoPanel.tabs.InfoTab;
import com.liquidpixel.main.ui.view.windows.UniversalWindow;
import com.liquidpixel.main.ui.view.workOrder.mocks.WorkOrderItemUIMock;
import com.liquidpixel.main.ui.view.workOrder.mocks.WorkOrderUIMock;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import com.kotcrab.vis.ui.widget.VisScrollPane;

/**
 * A test screen for UI components that can be hot reloaded using VisUI
 */
public class UiTestScreen implements Screen {
    private Stage stage;
    private VisTable rootTable;
    private VisTable contentTable;
    private Array<TestUITab> uiTabs;
    private String defaultTabName = "Farm";

    public UiTestScreen() {
        if (!VisUI.isLoaded()) {
            Skin skin = new Skin(Gdx.files.internal("assets/ui/skin/golden-gate/skin.json"));
            VisUI.load(skin);
        }

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        rootTable = new VisTable();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        uiTabs = new Array<>();
        uiTabs.add(new SampleUITab());
        uiTabs.add(new WorkOrderItemUIMock());
        uiTabs.add(new WorkOrderUIMock());
        uiTabs.add(new InfoTab());
        uiTabs.add(new StorageMockPanel());
        uiTabs.add(new PeopleMockPanel());
        uiTabs.add(new TableUIMock());
        uiTabs.add(new ItemSelectionMock());
//        uiTabs.add(new FarmUIMock());
        uiTabs.add(new WindowMockUI());
        uiTabs.add(new UniversalWindow("Universal Window"));

        setupUi();
    }

    /**
     * Set up the UI components - this method will be called on hot reload
     */
    public void setupUi() {
        rootTable.clear();

        // Create left panel for tabs
        VisTable leftPanel = new VisTable();
        leftPanel.top();

        // Create content panel
        contentTable = new VisTable();
        contentTable.top();

        // Create a scroll pane for the left panel
        VisScrollPane leftScroll = new VisScrollPane(leftPanel);
        leftScroll.setFadeScrollBars(false);
        leftScroll.setScrollingDisabled(true, false);

        // Create a scroll pane for the content
        VisScrollPane contentScroll = new VisScrollPane(contentTable);
        contentScroll.setFadeScrollBars(false);

        // Add tab buttons to left panel
        VisLabel tabsLabel = new VisLabel("UI Components");
        tabsLabel.setFontScale(1.2f);
        leftPanel.add(tabsLabel).padTop(10).padBottom(15).expandX().center().row();

        // Add tab buttons
        TestUITab defaultTab = null;
        for (final TestUITab tab : uiTabs) {
            VisTextButton tabButton = new VisTextButton(tab.getTabTitle());
            tabButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showTab(tab);
                }
            });

            leftPanel.add(tabButton).fillX().expandX().padBottom(5).padLeft(10).padRight(10).row();

            // Set default tab
            if (tab.getTabTitle().equals(defaultTabName)) {
                defaultTab = tab;
            }
        }

        // Add reload button at the bottom left
        VisTextButton reloadButton = new VisTextButton("Reload UI");
        reloadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setupUi();
            }
        });

        leftPanel.add().expand().row(); // Push the reload button to the bottom
        leftPanel.add(reloadButton).fillX().padBottom(10).padLeft(10).padRight(10).row();

        // Create split pane with left panel and content
        VisSplitPane splitPane = new VisSplitPane(leftScroll, contentScroll, false);
        splitPane.setSplitAmount(0.2f); // 20% for left panel

        // Add split pane to root table
        rootTable.add(splitPane).expand().fill();
        rootTable.setBackground("blue-main");

        // Show default tab
        if (defaultTab != null) {
            showTab(defaultTab);
        } else if (uiTabs.size > 0) {
            showTab(uiTabs.first());
        }
    }

    private void showTab(TestUITab tab) {
        contentTable.clear();
        contentTable.add(new VisLabel(tab.getTabTitle())).padTop(10).padBottom(15).expandX().center().row();
        tab.setupUI(contentTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        update(delta);
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    private void update(float delta) {
        for (TestUITab window : uiTabs) {
            if (window instanceof Updatable) {
                ((Updatable) window).update();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        // Only dispose VisUI if this is the last screen using it
        // If other parts of your app use VisUI, handle disposal elsewhere
        if (VisUI.isLoaded())
            VisUI.dispose();
    }

    /**
     * Interface for UI test tabs
     */
    public interface TestUITab {
        String getTabTitle();

        void setupUI(VisTable contentTable);
    }
}
