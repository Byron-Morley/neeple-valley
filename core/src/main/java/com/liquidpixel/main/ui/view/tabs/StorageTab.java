package com.liquidpixel.main.ui.view.tabs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.ui.IWindowService;
import com.liquidpixel.main.ui.view.infoPanel.storage.StorageSlotUI;
import com.liquidpixel.main.ui.view.interfaces.IUniversalTab;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.List;

import static com.liquidpixel.main.screens.WorldScreen.UI_SCALE;

public class StorageTab extends VisTable implements IUniversalTab {

    IWindowService windowService;
    StorageComponent storageComponent;
    VisTable contentTable;
    IItemService itemService;

    StorageComponent storage;

    public StorageTab(Entity entity, IWindowService windowService, IItemService itemService) {
        super();
        this.windowService = windowService;
        this.itemService = itemService;
        this.storage = entity.getComponent(StorageComponent.class);
        init();
    }

    public void init() {

        defaults().pad(5 / UI_SCALE, 5 / UI_SCALE, 5 / UI_SCALE, 5 / UI_SCALE);
        row().fill().expandX().expandY();

        VisTable root = new VisTable();
        add(root);

        contentTable = new VisTable();
        contentTable.defaults().pad(5).size(60);
        root.add(contentTable).fill().expand();

        contentTable.setBackground("blue-main");
        pack();

        setPosition(
            (Gdx.graphics.getWidth() - getWidth()) / 2,
            (Gdx.graphics.getHeight() - getHeight()) / 2
        );
        update();
    }

    public void update() {
        renderStorage();
        pack();
    }

    private void renderStorage() {
        contentTable.clear();


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
//        Image image = createStorageImage(item);
//        StorageSlotUI storageSlotUI = new StorageSlotUI(image, item.getQuantity() + "/" + item.getStackSize());
//        contentTable.add(storageSlotUI);
    }

    private void addEmptySlot() {
        StorageSlotUI emptySlot = new StorageSlotUI(null, "");
        contentTable.add(emptySlot);
    }

    @Override
    public String getTabTitle() {
        return "Storage";
    }

    @Override
    public VisTable getContentTable() {
        return this;
    }

    @Override
    public void updateContent() {
        update();
    }
}
