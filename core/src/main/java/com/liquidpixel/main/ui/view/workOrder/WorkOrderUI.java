package com.liquidpixel.main.ui.view.workOrder;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.liquidpixel.main.interfaces.IGet;
import com.liquidpixel.main.interfaces.services.ISettlementService;
import com.liquidpixel.main.interfaces.work.IWorkOrder;
import com.liquidpixel.main.interfaces.Updatable;
import com.liquidpixel.main.model.storage.WorkOrder;
import com.liquidpixel.main.ui.common.ReuseableWindow;
import com.kotcrab.vis.ui.widget.VisWindow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.liquidpixel.main.screens.WorldScreen.UI_SCALE;

public class WorkOrderUI extends ReuseableWindow implements IGet<Group>, Updatable {

    ISettlementService settlementService;
    WorkOrderUIPresenter presenter;


    public WorkOrderUI(ISettlementService settlementService) {
        super("Work Orders");
        this.settlementService = settlementService;
        setVisible(false);
        addCloseButton();
        presenter = new WorkOrderUIPresenter();

        setPosition(250 / UI_SCALE, 500 / UI_SCALE);
        defaults().pad(10, 5, 10, 5);
        row().fill().expandX().expandY();
        add(presenter).width(500 / UI_SCALE).height(600 / UI_SCALE);
        pack();
        updateContent();
    }

    public void updateContent() {
        Map<WorkOrder.State, List<IWorkOrder>> workOrdersByState = new HashMap<>();
        for (WorkOrder.State state : WorkOrder.State.values()) {
            List<IWorkOrder> orders = settlementService.getWorkOrdersByState(state);
            workOrdersByState.put(state, orders);
        }
        presenter.setWorkOrders(workOrdersByState);
        pack();
    }

    @Override
    public VisWindow get() {
        return this;
    }

    @Override
    public void update() {
        updateContent();

    }
}
