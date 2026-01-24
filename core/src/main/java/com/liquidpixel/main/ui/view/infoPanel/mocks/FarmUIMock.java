package com.liquidpixel.main.ui.view.infoPanel.mocks;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.liquidpixel.main.components.items.FarmComponent;
import com.liquidpixel.main.factories.ModelFactory;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.interfaces.Updatable;
import com.liquidpixel.main.model.item.Item;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.sprite.model.GameSprite;
import com.liquidpixel.main.ui.UiTestScreen;
import com.liquidpixel.main.ui.common.ReuseableWindow;
import com.liquidpixel.main.ui.model.farming.FarmItem;
import com.liquidpixel.main.ui.view.listItems.FarmItemUI;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class FarmUIMock extends ReuseableWindow implements UiTestScreen.TestUITab, Updatable {

    public FarmUIMock() {
        super("Farming Mock");
    }

    @Override
    public void update() {

    }

    @Override
    public String getTabTitle() {
        return "Farm";
    }

    @Override
    public void setupUI(VisTable table) {

        table.debugAll();
        table.defaults().pad(5);

        VisTextButton addButton = new VisTextButton("Add");
        addButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Add button clicked - check ItemSelectionMock tab for item selection UI");
            }
        });

        table.add(addButton).left().padBottom(10);
        table.row();

        // Create scroll pane for farm items like WorkOrderTabContent
        VisTable farmItemListTable = new VisTable();
        farmItemListTable.top();

        for (int i = 0; i < 5; i++) {
            FarmItem farmItem = new FarmItem(getItem("wood", 10), 10, 10);
            FarmItemUI farmItemUI = new FarmItemUI(farmItem, null, null);
            farmItemListTable.add(farmItemUI).growX().height(80).padBottom(5).top().row();
        }

        VisScrollPane scrollPane = new VisScrollPane(farmItemListTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);

        table.add(scrollPane).grow().padRight(15).minHeight(350).top().row();

        setSize(500, getHeight());
        pack();
    }


    private IStorageItem getItem(String name, int quantity) {
        System.out.println("getItem: " + name);
        Item item = ModelFactory.getItemsModel().get("test/" + name);
        GameSprite sprite = null;
        return new StorageItem(name, quantity, item.getStackSize(), sprite);
    }
}
