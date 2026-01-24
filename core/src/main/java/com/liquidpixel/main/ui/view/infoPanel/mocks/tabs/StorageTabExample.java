package com.liquidpixel.main.ui.view.infoPanel.mocks.tabs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.liquidpixel.main.ui.view.interfaces.IUniversalTab;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.Arrays;
import java.util.List;

/**
 * Simple Storage tab that displays storage items with quantities
 */
public class StorageTabExample implements IUniversalTab {

    private final VisTable contentTable;
    private final VisTable itemListTable;
    private VisScrollPane scrollPane;
    private final VisLabel capacityLabel;

    public StorageTabExample() {
        contentTable = new VisTable();
        itemListTable = new VisTable();
        capacityLabel = new VisLabel("Capacity: 45/100");

        setupUI();
    }

    private void setupUI() {
        contentTable.top().left();
        contentTable.pad(10);

        // Header
        VisLabel titleLabel = new VisLabel("Storage Contents");
        titleLabel.setFontScale(1.1f);
        titleLabel.setColor(Color.WHITE);

        capacityLabel.setColor(Color.CYAN);

        // Create scroll pane for items
        scrollPane = new VisScrollPane(itemListTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);

        // Layout
        contentTable.add(titleLabel).expandX().left().padBottom(5).row();
        contentTable.add(capacityLabel).expandX().left().padBottom(10).row();
        contentTable.add(scrollPane).grow().minHeight(300).row();

        // Add sample items
        updateItemList();
    }

    private void updateItemList() {
        itemListTable.clear();
        itemListTable.top();

        // Sample storage items
        List<StorageItem> items = Arrays.asList(
            new StorageItem("Wood", 25, 50),
            new StorageItem("Stone", 15, 30),
            new StorageItem("Iron Ore", 5, 20),
            new StorageItem("Food", 12, 40),
            new StorageItem("Tools", 3, 10)
        );

        for (StorageItem item : items) {
            VisTable itemRow = createItemRow(item);
            itemListTable.add(itemRow).growX().height(40).padBottom(2).row();
        }
    }

    private VisTable createItemRow(StorageItem item) {
        VisTable row = new VisTable();
        row.setBackground("window-bg");
        row.pad(5);

        // Item name
        VisLabel nameLabel = new VisLabel(item.name);
        nameLabel.setColor(Color.WHITE);

        // Quantity
        VisLabel quantityLabel = new VisLabel(item.quantity + "/" + item.maxQuantity);
        quantityLabel.setColor(item.quantity >= item.maxQuantity ? Color.RED : Color.LIGHT_GRAY);

        // Use button
        VisTextButton useButton = new VisTextButton("Use");
        useButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Using: " + item.name);
            }
        });

        // Layout: Name | Quantity | Button
        row.add(nameLabel).expandX().left().padRight(10);
        row.add(quantityLabel).padRight(10);
        row.add(useButton).width(50);

        return row;
    }

    @Override
    public String getTabTitle() {
        return "Storage";
    }

    @Override
    public VisTable getContentTable() {
        return contentTable;
    }

    @Override
    public void updateContent() {
        // In a real implementation, this would update with actual storage data
        // For now, just simulate changing capacity
        int used = (int)(Math.random() * 20) + 40;
        capacityLabel.setText("Capacity: " + used + "/100");
        capacityLabel.setColor(used > 80 ? Color.RED : Color.CYAN);
    }

    private static class StorageItem {
        String name;
        int quantity;
        int maxQuantity;

        StorageItem(String name, int quantity, int maxQuantity) {
            this.name = name;
            this.quantity = quantity;
            this.maxQuantity = maxQuantity;
        }
    }
}
