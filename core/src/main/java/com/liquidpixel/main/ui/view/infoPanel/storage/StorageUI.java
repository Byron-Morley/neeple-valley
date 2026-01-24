package com.liquidpixel.main.ui.view.infoPanel.storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.interfaces.*;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.ui.IWindowService;
import com.liquidpixel.main.services.ui.UIService;
import com.liquidpixel.main.ui.common.ReuseableWindow;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

import java.util.List;

import static com.liquidpixel.main.screens.WorldScreen.UI_SCALE;

public class StorageUI extends ReuseableWindow implements IGet<Group>, Updatable {

    IWindowService windowService;
    StorageComponent storageComponent;
    VisTable contentTable;
    IItemService itemService;
    UIService uiService;

    public StorageUI(IWindowService windowService, IItemService itemService, UIService uiService) {
        super("Storage Menu");
        this.windowService = windowService;
        this.itemService = itemService;
        this.uiService = uiService;
        init();
    }

    public void init() {
        this.setMovable(false);
        this.setVisible(false);
        addCloseButton();

        defaults().pad(5 / UI_SCALE, 5 / UI_SCALE, 5 / UI_SCALE, 5 / UI_SCALE);
        row().fill().expandX().expandY();

        VisTable root = new VisTable();
        add(root);

        contentTable = new VisTable();
        contentTable.defaults().pad(5).size(60);
        root.add(contentTable).fill().expand();

        pack();

        setPosition(
            (Gdx.graphics.getWidth() - getWidth()) / 2,
            (Gdx.graphics.getHeight() - getHeight()) / 2
        );
    }

    public void update() {
        renderStorage(storageComponent);
        pack();
    }

    private void renderStorage(StorageComponent storage) {
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
    public VisWindow get() {
        return this;
    }
}
