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
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class TableUIMock implements UiTestScreen.TestUITab, Updatable {

    @Override
    public String getTabTitle() {
        return "TableMock";
    }

    @Override
    public void setupUI(VisTable contentTable) {
        VisTable container = new VisTable();
        container.defaults().pad(10, 5, 10, 5);

        VisTable tableContainer = createTableWithData();

        container.row().fill().expandX().expandY();
        container.add(tableContainer);

        contentTable.add(container).grow();
    }

    private VisTable createTableWithData() {
        VisTable table = new VisTable();
        table.defaults().pad(5);

        // Add "Add" button at the top (left aligned)
        VisTextButton addButton = new VisTextButton("Add");
        addButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Add button clicked - check ItemSelectionMock tab for item selection UI");
            }
        });
        table.add(addButton).left().padBottom(10);
        table.add().expandX(); // Empty cell to push button left
        table.add(); // Empty cell
        table.add(); // Empty cell
        table.row();

        // Create table header
        table.add(new VisLabel("No.")).padRight(20);
        table.add(new VisLabel("Item")).padRight(20);
        table.add(new VisLabel("Description")).expandX().fillX();
        table.add(new VisLabel("Time")).padRight(20);
        table.row();

        // Add separator line
        table.row().padTop(5).padBottom(5);

        // Create three rows with IStorageItem objects
        addTableRowWithHeader(table, 1, getItem("wood", 25), 2, 5);
        addTableRowWithHeader(table, 2, getItem("iron", 15), 1, 12);
        addTableRowWithHeader(table, 3, getItem("stone", 40), 3, 8);

        return table;
    }

    private void addTableRowWithHeader(VisTable table, int number, IStorageItem item, int days, int hours) {
        // Add header row first
        addHeaderRow(table, number);

        // Then add the main data row
        addTableRow(table, number, item, days, hours);
    }

    private void addHeaderRow(VisTable table, int rowNumber) {
        // Create header with close button spanning all columns
        VisTable headerTable = new VisTable();

        // Empty space to push close button to the right
        headerTable.add().expandX().fillX();

        // Close button with X
        VisTextButton closeButton = new VisTextButton("×");
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Close button clicked for row " + rowNumber);
            }
        });

        headerTable.add(closeButton).size(20, 20).padRight(5);

        // Add header table spanning all columns, with height about half of main row
        table.add(headerTable).colspan(4).expandX().fillX().height(16).padBottom(2);
        table.row();
    }

    private void addTableRow(VisTable table, int number, IStorageItem item, int days, int hours) {
        // Number column
        VisLabel numberLabel = new VisLabel(String.valueOf(number));
        table.add(numberLabel).padRight(20).center();

        // Image column
        Image itemImage = createItemImage(item);
        table.add(itemImage).padRight(20).size(32, 32).center();

        // Text column - create a nested table for two lines
        VisTable textTable = new VisTable();
        VisLabel nameLabel = new VisLabel(item.getName());
        VisLabel quantityLabel = new VisLabel("x" + item.getQuantity());

        textTable.add(nameLabel).expandX().fillX().left().row();
        textTable.add(quantityLabel).expandX().fillX().left();

        table.add(textTable).expandX().fillX().center();

        // Time column - create a nested table for two lines
        VisTable timeTable = new VisTable();
        VisLabel daysLabel = new VisLabel(days + " days");
        VisLabel hoursLabel = new VisLabel(hours + " hours");

        timeTable.add(daysLabel).expandX().fillX().left().row();
        timeTable.add(hoursLabel).expandX().fillX().left();

        table.add(timeTable).padRight(20).center();

        table.row();
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
