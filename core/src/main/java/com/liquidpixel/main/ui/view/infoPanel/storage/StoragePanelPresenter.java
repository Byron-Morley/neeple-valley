package com.liquidpixel.main.ui.view.infoPanel.storage;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.liquidpixel.main.services.items.StorageHelper;

import java.util.List;


public class StoragePanelPresenter extends VisTable {
    VisTable contentTable;
    StorageComponent storageComponent;

    public StoragePanelPresenter() {
        super();
        init();
    }

    private void init() {
        contentTable = new VisTable();
        VisLabel titleLabel = new VisLabel("Storage");

        // Header row
        contentTable.add(titleLabel).left().expandX();
        contentTable.row();
        contentTable.defaults().pad(5).size(60);
        add(contentTable).fill().expand();
    }

    public void update() {
        renderStorage(storageComponent);
        pack();
    }

    private void renderStorage(StorageComponent storage) {
        contentTable.clear();
        if (storageComponent == null) return;
        if (storage != null) {
            int totalSlots = storage.getSlots();
            int filledSlots = 0;

            List<IStorageItem> itemStacks = storage.calculateItemStacks();
            for (IStorageItem itemStack : itemStacks) {
                addSlotContent(itemStack);
                filledSlots++;

                if (filledSlots % 4 == 0) {
                    contentTable.row();
                }
            }

            for (int i = filledSlots; i < totalSlots; i++) {
                addEmptySlot();
                if ((i + 1) % 4 == 0) {
                    contentTable.row();
                }
            }
        }
    }

    private void addSlotContent(IStorageItem item) {
        Image image = StorageHelper.createStorageImage(item, null);
        StorageSlotUI storageSlotUI = new StorageSlotUI(image, item.getQuantity() + "/" + item.getStackSize());
        contentTable.add(storageSlotUI);
    }

    private void addEmptySlot() {
        StorageSlotUI emptySlot = new StorageSlotUI(null, "");
        contentTable.add(emptySlot);
    }

    public void setStorageComponent(StorageComponent storageComponent) {
        this.storageComponent = storageComponent;
        update();
    }
}
