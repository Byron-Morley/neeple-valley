package com.liquidpixel.main.ui.view.infoPanel.mocks;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

public class WindowListTab extends Tab {
    private final WindowListUI.WindowCategory category;
    private final WindowListTabContent content;
    private final VisTable contentTable;

    public WindowListTab(WindowListUI.WindowCategory category, WindowListTabContent content) {
        super(false, false);
        this.category = category;
        this.content = content;
        this.contentTable = new VisTable();
        contentTable.add(content.getContentTable()).grow();
    }

    public WindowListUI.WindowCategory getCategory() {
        return category;
    }

    @Override
    public String getTabTitle() {
        return category.name();
    }

    @Override
    public Table getContentTable() {
        return contentTable;
    }
}
