package com.liquidpixel.main.ui.view.workOrder.mocks;

import com.liquidpixel.main.interfaces.work.IWorkOrder;
import com.liquidpixel.main.interfaces.Updatable;
import com.liquidpixel.main.model.storage.WorkOrder;
import com.liquidpixel.main.ui.UiTestScreen;
import com.liquidpixel.main.ui.view.workOrder.WorkOrderUIPresenter;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.liquidpixel.main.screens.WorldScreen.UI_SCALE;
import static com.liquidpixel.main.ui.view.workOrder.mocks.WorkOrderHelper.createOrder;

public class WorkOrderUIMock implements UiTestScreen.TestUITab, Updatable {

    private WorkOrderUIPresenter presenter;
    private final Map<WorkOrder.State, List<IWorkOrder>> workOrdersByState = new HashMap<>();

    @Override
    public String getTabTitle() {
        return "WorkOrderUI";
    }

    @Override
    public void setupUI(VisTable contentTable) {
        VisTable container = new VisTable();
        container.debugAll();
        container.setPosition(250 / UI_SCALE, 500 / UI_SCALE);
        container.defaults().pad(10, 5, 10, 5);

        // Create presenter
        presenter = new WorkOrderUIPresenter();
        mockOrderData();
        presenter.setWorkOrders(workOrdersByState);

        container.row().fill().expandX().expandY();
        container.add(presenter).width(500 / UI_SCALE).height(600 / UI_SCALE);

        contentTable.add(container).grow();
    }

    private void mockOrderData() {
        for (WorkOrder.State state : WorkOrder.State.values()) {
            List<IWorkOrder> orders = new ArrayList<>();

            int orderCount = 2;
            switch (state) {
                case PENDING:
                    orderCount = 3;
                    break;
                case RUNNING:
                    orderCount = 2;
                    break;
                case COMPLETED:
                    orderCount = 8;
                    break;
                case FAILED:
                    orderCount = 3;
                    break;
            }

            for (int i = 0; i < orderCount; i++) {
                IWorkOrder order = createOrder();
                order.setState(state);
                orders.add(order);
            }

            workOrdersByState.put(state, orders);
        }
    }

    @Override
    public void update() {
//        presenter.updateContent();
    }
}
