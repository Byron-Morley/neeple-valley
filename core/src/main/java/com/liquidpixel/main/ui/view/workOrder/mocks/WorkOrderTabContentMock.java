package com.liquidpixel.main.ui.view.workOrder.mocks;

import com.liquidpixel.main.model.storage.WorkOrder;
import com.liquidpixel.main.ui.UiTestScreen;
import com.liquidpixel.main.ui.view.workOrder.WorkOrderTab;
import com.liquidpixel.main.ui.view.workOrder.WorkOrderTabContent;
import com.kotcrab.vis.ui.widget.VisTable;


public class WorkOrderTabContentMock implements UiTestScreen.TestUITab {
    @Override
    public String getTabTitle() {
        return "WorkOrderTabContent";
    }

    @Override
    public void setupUI(VisTable contentTable) {
       VisTable container = new VisTable();
       WorkOrderTabContent content = new WorkOrderTabContent(WorkOrder.State.PENDING);
       WorkOrderTab tab = new WorkOrderTab(WorkOrder.State.PENDING, content);

        container.add();
        contentTable.add(container);
    }
}
