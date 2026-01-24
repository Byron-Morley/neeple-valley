package com.liquidpixel.main.ui.view.infoPanel.mocks;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.liquidpixel.main.factories.ModelFactory;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.interfaces.Updatable;
import com.liquidpixel.main.model.item.Item;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.sprite.model.GameSprite;
import com.liquidpixel.main.ui.UiTestScreen;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class ItemSelectionMock implements UiTestScreen.TestUITab, Updatable {

    @Override
    public String getTabTitle() {
        return "ItemSelection";
    }

    @Override
    public void setupUI(VisTable contentTable) {
        VisTable container = new VisTable();
        container.defaults().pad(10, 5, 10, 5);

        VisTable selectionContainer = createItemSelectionList();

        container.row().fill().expandX().expandY();
        container.add(selectionContainer);

        contentTable.add(container).grow();
    }

    private VisTable createItemSelectionList() {
        VisTable mainTable = new VisTable();

        // Add title
        VisLabel titleLabel = new VisLabel("Select Item");
        titleLabel.setFontScale(1.2f);
        mainTable.add(titleLabel).padBottom(10);
        mainTable.row();

        // Create scrollable list of items
        VisTable itemList = new VisTable();
        itemList.defaults().pad(5);

        // Add some example items to choose from
        String[] availableItems = {"wood", "iron", "stone", "gold", "cloth", "food"};

        for (String itemName : availableItems) {
            try {
                IStorageItem item = getItem(itemName, 1);

                // Create a row for each item
                VisTable itemRow = new VisTable();

                // Add item image
                Image itemImage = createItemImage(item);
                itemRow.add(itemImage).size(32, 32).padRight(15);

                // Add item name
                VisLabel itemLabel = new VisLabel(item.getName());
                itemRow.add(itemLabel).expandX().fillX().left();

                // Add select button
                VisTextButton selectButton = new VisTextButton("Select");
                selectButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        System.out.println("Selected item: " + item.getName());
                    }
                });
                itemRow.add(selectButton);

                // Add the row to the list
                itemList.add(itemRow).expandX().fillX().padBottom(8);
                itemList.row();

            } catch (Exception e) {
                System.out.println("Could not load item: " + itemName);
            }
        }

        // Create scroll pane
        VisScrollPane scrollPane = new VisScrollPane(itemList);
        scrollPane.setFadeScrollBars(false);

        // Add scroll pane to main table
        mainTable.add(scrollPane).expand().fill().minHeight(300);

        return mainTable;
    }

    private Image createItemImage(IStorageItem item) {
        try {
            // Use the sprite from the IStorageItem
            GameSprite sprite = item.getSprite();
            Image image = new Image(sprite);
            image.setSize(32, 32);
            return image;
        } catch (Exception e) {
            // Fallback to a default texture if item sprite not found
            System.out.println("Could not load sprite for: " + item.getName());
            try {
//                GameSprite fallbackSprite = SpriteFactory.getSprite("missing");
                GameSprite fallbackSprite = null;
                Image fallbackImage = new Image(fallbackSprite);
                fallbackImage.setSize(32, 32);
                return fallbackImage;
            } catch (Exception fallbackException) {
                // Return empty image if no fallback available
                return new Image();
            }
        }
    }

    private IStorageItem getItem(String name, int quantity) {
        System.out.println("getItem: " + name);
        Item item = ModelFactory.getItemsModel().get("test/" + name);
//        GameSprite sprite = SpriteFactory.getSprite(item.getSpriteName());
        GameSprite sprite = null;
        return new StorageItem(name, quantity, item.getStackSize(), sprite);
    }

    @Override
    public void update() {
        // Update logic if needed
    }
}
