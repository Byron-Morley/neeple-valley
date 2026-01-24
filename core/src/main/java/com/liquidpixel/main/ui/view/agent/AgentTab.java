package com.liquidpixel.main.ui.view.agent;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

public class AgentTab extends Tab {
    private final AgentTabContent content;
    private final VisTable contentTable;

    public AgentTab(AgentTabContent content) {
        super(false, false);
        this.content = content;
        this.contentTable = new VisTable();
        contentTable.add(content.getContentTable()).grow();
    }

    @Override
    public String getTabTitle() {
        return "All Agents";
    }

    @Override
    public Table getContentTable() {
        return contentTable;
    }
}
