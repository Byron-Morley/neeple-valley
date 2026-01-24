package com.liquidpixel.main.ui.view.workOrder.mocks;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.components.agent.AgentComponent;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.main.factories.ModelFactory;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.interfaces.work.IWorkOrder;
import com.liquidpixel.main.model.item.Item;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.sprite.model.GameSprite;
import com.liquidpixel.main.model.storage.WorkOrder;

import static com.liquidpixel.main.model.Work.TRANSPORT;

public class WorkOrderHelper {

    public static IWorkOrder createOrder() {
        Entity sourceEntity = new Entity();
        sourceEntity.add(new ItemComponent("storage/warehouse", 1));
        Entity destinationEntity = new Entity();
        destinationEntity.add(new ItemComponent("storage/warehouse", 1));

        Entity workerEntity = new Entity();
        workerEntity.add(new AgentComponent("worker"));

        Item item = ModelFactory.getItemsModel().get("resources/wood_log");
//        GameSprite sprite = SpriteFactory.getSprite(item.getSpriteName());
        GameSprite sprite = null;
        IStorageItem storageItem = new StorageItem("resources/wood_log", 50, item.getStackSize(), sprite);

        IWorkOrder workOrder = new WorkOrder(
            sourceEntity,
            destinationEntity,
            storageItem,
            TRANSPORT
        );

        return workOrder;
    }
}
