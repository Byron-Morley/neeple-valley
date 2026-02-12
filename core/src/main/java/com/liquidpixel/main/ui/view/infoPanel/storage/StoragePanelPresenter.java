package com.liquidpixel.main.ui.view.infoPanel.storage;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.services.items.StorageHelper;

import java.util.List;

public class StoragePanelPresenter extends VisTable {
    VisTable contentTable;
    StorageComponent storageComponent;

    private Entity entity;

    private VisTable headerTable;
    private VisTable slotsTable;

    private VisLabel priorityValueLabel;
    private VisTextButton minusButton;
    private VisTextButton plusButton;

    private Listener listener;

    public StoragePanelPresenter() {
        super();
        init();
    }

    private void init() {
        contentTable = new VisTable();
        headerTable = new VisTable();
        slotsTable = new VisTable();

        VisLabel titleLabel = new VisLabel("Storage");

        minusButton = new VisTextButton("-");
        plusButton = new VisTextButton("+");
        priorityValueLabel = new VisLabel("0");

        // Header row: "Storage" .......... [-] Priority: N [+]
        headerTable.add(titleLabel).left().expandX();

        headerTable.add(minusButton).width(28).height(24).padRight(4);
        headerTable.add(new VisLabel("Priority:")).padRight(4);
        headerTable.add(priorityValueLabel).width(20).padRight(4);
        headerTable.add(plusButton).width(28).height(24);

        contentTable.add(headerTable).growX().row();

        // Slots grid under header
        contentTable.row();
        slotsTable.defaults().pad(5).size(60);
        contentTable.add(slotsTable).growX().left().row();

        add(contentTable).fill().expand();

        wirePriorityButtons();
    }

    private void wirePriorityButtons() {
        minusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changePriority(-1);
            }
        });

        plusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changePriority(+1);
            }
        });
    }

    private void changePriority(int delta) {
        if (storageComponent == null) return;

        int oldPriority = storageComponent.getPriority();
        int newPriority = MathUtils.clamp(oldPriority + delta, 0, 10);

        if (newPriority == oldPriority) return; // already at clamp edge

        storageComponent.setPriority(newPriority);
        priorityValueLabel.setText(String.valueOf(newPriority));

        if (listener != null) {
            listener.onPriorityChanged(entity, storageComponent, newPriority);
        }
    }

    public void update() {
        renderStorage(storageComponent);
        if (storageComponent != null) {
            priorityValueLabel.setText(String.valueOf(MathUtils.clamp(storageComponent.getPriority(), 0, 10)));
        }
        pack();
    }

    private void renderStorage(StorageComponent storage) {
        slotsTable.clear();
        if (storageComponent == null) return;

        if (storage != null) {
            int totalSlots = storage.getSlots();
            int filledSlots = 0;

            List<IStorageItem> itemStacks = storage.calculateItemStacks();
            for (IStorageItem itemStack : itemStacks) {
                addSlotContent(itemStack);
                filledSlots++;

                if (filledSlots % 4 == 0) {
                    slotsTable.row();
                }
            }

            for (int i = filledSlots; i < totalSlots; i++) {
                addEmptySlot();
                if ((i + 1) % 4 == 0) {
                    slotsTable.row();
                }
            }
        }
    }

    private void addSlotContent(IStorageItem item) {
        Image image = StorageHelper.createStorageImage(item, null);
        StorageSlotUI storageSlotUI = new StorageSlotUI(image, item.getQuantity() + "/" + item.getStackSize());
        slotsTable.add(storageSlotUI);
    }

    private void addEmptySlot() {
        StorageSlotUI emptySlot = new StorageSlotUI(null, "");
        slotsTable.add(emptySlot);
    }

    public void setStorageComponent(StorageComponent storageComponent) {
        this.storageComponent = storageComponent;

        // Keep the model sane if it was loaded with an out-of-range value
        if (this.storageComponent != null) {
            this.storageComponent.setPriority(MathUtils.clamp(this.storageComponent.getPriority(), 0, 10));
        }

        update();
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onPriorityChanged(Entity entity, StorageComponent storageComponent, int newPriority);
    }
}
