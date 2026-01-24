package com.liquidpixel.main.ui.view.windows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.liquidpixel.main.engine.GameState;
import com.liquidpixel.main.interfaces.IGet;
import com.liquidpixel.main.interfaces.Updatable;
import com.liquidpixel.main.ui.UiTestScreen;
import com.liquidpixel.main.ui.common.ReuseableWindow;
import com.liquidpixel.main.ui.view.interfaces.IUniversalTab;
import com.liquidpixel.main.ui.view.infoPanel.mocks.tabs.InfoTabExample;
import com.liquidpixel.main.ui.view.infoPanel.mocks.tabs.StorageTabExample;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.liquidpixel.main.screens.WorldScreen.UI_SCALE;

public class UniversalWindow extends ReuseableWindow implements IGet<Group>, Updatable, UiTestScreen.TestUITab {

    private final TabbedPane tabbedPane;
    private final VisTable containerTable;
    private final VisTable contentTable;
    private final List<IUniversalTab> universalTabs;

    public UniversalWindow(String title) {
        super(title);
        setVisible(false);
        addCloseButton();

        universalTabs = new ArrayList<>();
        tabbedPane = new TabbedPane();
        tabbedPane.addListener(new TabbedPaneAdapter() {
            @Override
            public void switchedTab(Tab tab) {
                updateActiveTabContent();
            }
        });

        // Create container for the tabbed pane
        containerTable = new VisTable();
        containerTable.add(tabbedPane.getTable()).growX().height(30).row();

        // Create content area below the tabs
        contentTable = new VisTable();
        containerTable.add(contentTable).grow().top().row();

        // Style the container

        containerTable.setBackground("blue-main");
        // Position and size the window
        setPosition(250 / UI_SCALE, 500 / UI_SCALE);
        defaults().pad(10, 5, 10, 5);
        row().fill().expandX().expandY();
        add(containerTable).width(500 / UI_SCALE).height(600 / UI_SCALE);
        pack();
    }

    @Override
    public void addCloseButton() {
        Label titleLabel = this.getTitleLabel();
        Table titleTable = this.getTitleTable();
        VisImageButton closeButton = new VisImageButton("close-window");
        titleTable.add(closeButton).padRight(-this.getPadRight() + 0.7F);
        closeButton.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                // Clear all tabs when closing
                clearTabs();
                GameState.setPaused(false);
                setVisible(false);
            }
        });
        closeButton.addListener(new ClickListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.cancel();
                return true;
            }
        });
        if (titleLabel.getLabelAlign() == 1 && titleTable.getChildren().size == 2) {
            titleTable.getCell(titleLabel).padLeft(closeButton.getWidth() * 2.0F);
        }
    }

    /**
     * Add a tab to the window
     */
    public void addTab(IUniversalTab universalTab) {
        universalTabs.add(universalTab);
        UniversalTabWrapper wrapper = new UniversalTabWrapper(universalTab);
        tabbedPane.add(wrapper);
        universalTab.onTabAdded();

        // If this is the first tab, make it active
        if (universalTabs.size() == 1) {
            tabbedPane.switchTab(wrapper);
        }

        pack();
    }

    /**
     * Remove a tab from the window
     */
    public void removeTab(IUniversalTab universalTab) {
        universalTabs.remove(universalTab);

        // Find and remove the wrapper
        for (Tab tab : tabbedPane.getTabs()) {
            if (tab instanceof UniversalTabWrapper) {
                UniversalTabWrapper wrapper = (UniversalTabWrapper) tab;
                if (wrapper.getUniversalTab() == universalTab) {
                    tabbedPane.remove(wrapper);
                    universalTab.onTabRemoved();
                    break;
                }
            }
        }

        pack();
    }

    /**
     * Clear all tabs
     */
    public void clearTabs() {
        for (IUniversalTab tab : new ArrayList<>(universalTabs)) {
            removeTab(tab);
        }
    }

    private void updateActiveTabContent() {
        Tab activeTab = tabbedPane.getActiveTab();
        if (activeTab instanceof UniversalTabWrapper) {
            UniversalTabWrapper wrapper = (UniversalTabWrapper) activeTab;
            IUniversalTab universalTab = wrapper.getUniversalTab();

            contentTable.clear();
            contentTable.add(universalTab.getContentTable()).grow().top();
            universalTab.updateContent();

            contentTable.invalidateHierarchy();
            contentTable.validate();
        }
    }

    @Override
    public VisWindow get() {
        return this;
    }

    @Override
    public void update() {
        // Update all tabs
        for (IUniversalTab tab : universalTabs) {
            tab.updateContent();
        }
    }

    @Override
    public String getTabTitle() {
        return "Universal Window";
    }

    @Override
    public void setupUI(VisTable table) {
        table.add(this).expand().fill();
        setVisible(true);

        // Demo: Add some example tabs
        addTab(new InfoTabExample());
        addTab(new StorageTabExample());
    }

    /**
     * Wrapper class to bridge IUniversalTab to VisUI Tab
     */
    private static class UniversalTabWrapper extends Tab {
        private final IUniversalTab universalTab;

        public UniversalTabWrapper(IUniversalTab universalTab) {
            super(false, false);
            this.universalTab = universalTab;
        }

        public IUniversalTab getUniversalTab() {
            return universalTab;
        }

        @Override
        public String getTabTitle() {
            return universalTab.getTabTitle();
        }

        @Override
        public VisTable getContentTable() {
            return universalTab.getContentTable();
        }
    }
}
