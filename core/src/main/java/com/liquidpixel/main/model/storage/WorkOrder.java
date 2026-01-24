package com.liquidpixel.main.model.storage;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.components.agent.WorkerComponent;
import com.liquidpixel.main.interfaces.IStatusComponent;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.interfaces.work.IWorkOrder;
import com.liquidpixel.main.model.Work;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.main.model.status.WorkState;
import com.liquidpixel.main.utils.Mappers;

public class WorkOrder implements IWorkOrder {

    public State state;

    public enum State {
        PENDING,
        RUNNING,
        COMPLETED,
        FAILED
    }

    private int priority;
    private Work type;
    private IStatusComponent component;
    private IStorageItem item;
    private Entity origin;
    private Entity destination;
    private Entity worker;

    public WorkOrder(
        Entity origin,
        Entity destination,
        IStorageItem item,
        Work type
    ) {
        this.origin = origin;
        this.destination = destination;
        this.item = item;
        this.state = State.PENDING;
        this.type = type;
        priority = getPriority(destination);
    }

    private int getPriority(Entity destination) {
        if (Mappers.storage.has(destination)) {
            return Mappers.storage.get(destination).getPriority();
        }
        return 0;
    }

    public WorkOrder(
        Entity origin,
        Entity destination,
        IStorageItem item,
        Work type,
        int priority
    ) {
        this.origin = origin;
        this.destination = destination;
        this.item = item;
        this.state = State.PENDING;
        this.type = type;
        this.priority = priority;
    }

    public void start(Entity worker) {
        this.worker = worker;
        this.state = State.RUNNING;
        Mappers.worker.get(worker).setState(WorkerComponent.State.BUSY);
    }

    public String getItemName() {
        return item.getName();
    }

    public int getQuantity() {
        return item.getQuantity();
    }

    public Entity getOrigin() {
        return origin;
    }

    public Entity getDestination() {
        return destination;
    }

    public boolean isComplete() {
        return component.getState() == WorkState.COMPLETED;
    }

    public boolean isActive() {
        return !(State.COMPLETED == state || State.FAILED == state);
    }

    public Entity getWorker() {
        return worker;
    }

    public void setQuantity(int quantity) {
        this.item.setQuantity(quantity);
    }

    public IStorageItem getItem() {
        return new StorageItem(item.getName(), item.getQuantity(), item.getStackSize(), item.getSprite());
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public IStatusComponent getComponent() {
        return component;
    }

    public void setComponent(IStatusComponent component) {
        this.component = component;
    }

    public Work getType() {
        return type;
    }

    public int getPriority() {
        return priority;
    }
}
