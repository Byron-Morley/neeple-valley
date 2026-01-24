package com.liquidpixel.main.ui.view.workOrder;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.liquidpixel.main.model.storage.WorkOrder;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

public class WorkOrderTab extends Tab {
    private final WorkOrder.State state;
    private final WorkOrderTabContent content;
    private final VisTable contentTable;

    public WorkOrderTab(WorkOrder.State state, WorkOrderTabContent content) {
        super(false, false);
        this.state = state;
        this.content = content;
        this.contentTable = new VisTable();
        contentTable.add(content.getContentTable()).grow();
    }

    public WorkOrder.State getState() {
        return state;
    }

    @Override
    public String getTabTitle() {
        return state.name();
    }

    @Override
    public Table getContentTable() {
        return contentTable;
    }
}
