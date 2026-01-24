package com.liquidpixel.main.interfaces.work;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.interfaces.IStatusComponent;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.model.Work;
import com.liquidpixel.main.model.storage.WorkOrder;

public interface IWorkOrder {

    void start(Entity worker);

    Entity getOrigin();

    Entity getDestination();

    Entity getWorker();

    IStorageItem getItem();

    String getItemName();

    int getQuantity();

    void setQuantity(int quantity);

    boolean isActive();

    boolean isComplete();

    WorkOrder.State getState();

    void setState(WorkOrder.State state);

    void setComponent(IStatusComponent component);

    Work getType();

    int getPriority();
}
