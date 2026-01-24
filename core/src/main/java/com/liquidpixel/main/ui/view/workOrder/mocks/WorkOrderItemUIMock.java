package com.liquidpixel.main.ui.view.workOrder.mocks;

import com.liquidpixel.main.ui.UiTestScreen;
import com.liquidpixel.main.ui.view.workOrder.WorkOrderItemUI;
import com.kotcrab.vis.ui.widget.VisTable;

import static com.liquidpixel.main.ui.view.workOrder.mocks.WorkOrderHelper.createOrder;

public class WorkOrderItemUIMock implements UiTestScreen.TestUITab {
    @Override
    public String getTabTitle() {
        return "Work Order Item UI";
    }

    @Override
    public void setupUI(VisTable contentTable) {
        VisTable container = new VisTable();
        container.add(new WorkOrderItemUI(createOrder()));
        contentTable.add(container);
    }
}
