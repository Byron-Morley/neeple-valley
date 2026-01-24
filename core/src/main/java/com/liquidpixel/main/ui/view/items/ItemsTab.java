package com.liquidpixel.main.ui.view.items;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

public class ItemsTab extends Tab {
    private final String category;
    private final ItemsTabContent content;
    private final VisTable contentTable;

    public ItemsTab(String category, ItemsTabContent content) {
        super(false, false);
        this.category = category;
        this.content = content;
        this.contentTable = new VisTable();
        contentTable.add(content.getContentTable()).grow();
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String getTabTitle() {
        return category;
    }

    @Override
    public Table getContentTable() {
        return contentTable;
    }
}
