package com.liquidpixel.main.ui.view.infoPanel.mocks;

import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.factories.ModelFactory;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.interfaces.Updatable;
import com.liquidpixel.main.model.item.Item;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.sprite.model.GameSprite;
import com.liquidpixel.main.ui.UiTestScreen;
import com.liquidpixel.main.ui.view.infoPanel.storage.StoragePanelPresenter;
import com.kotcrab.vis.ui.widget.VisTable;

import static com.liquidpixel.main.screens.WorldScreen.UI_SCALE;

public class StorageMockPanel implements UiTestScreen.TestUITab, Updatable {

    StoragePanelPresenter presenter;

    @Override
    public String getTabTitle() {
        return "StoragePanel";
    }

    @Override
    public void setupUI(VisTable contentTable) {
        VisTable container = new VisTable();
        container.debugAll();
        container.setPosition(250 / UI_SCALE, 500 / UI_SCALE);
        container.defaults().pad(10, 5, 10, 5);

        // Create presenter
        presenter = new StoragePanelPresenter();
        presenter.setStorageComponent(mockData());
        container.row().fill().expandX().expandY();
        container.add(presenter).width(400 / UI_SCALE).height(500 / UI_SCALE);

        contentTable.add(container).grow();
    }

    private StorageComponent mockData() {
        StorageComponent storageComponent = new StorageComponent(4);

        IStorageItem wood = getItem("wood", 30);
        IStorageItem iron = getItem("iron", 30);
        storageComponent.addItem(wood);
        storageComponent.addItem(iron);

        return storageComponent;
    }

    private IStorageItem getItem(String name, int quantity) {
        Item item = ModelFactory.getItemsModel().get("test/" + name);
//        GameSprite sprite = SpriteFactory.getSprite(item.getSpriteName());
        GameSprite sprite = null;
        return new StorageItem(name, quantity, item.getStackSize(), sprite);
    }

    @Override
    public void update() {
        if (presenter != null) {
            presenter.update();
        }
    }
}
