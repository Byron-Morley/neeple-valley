package com.liquidpixel.main.ui.view.items;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.main.utils.Mappers;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

public class ItemItemUI extends VisTable {

    private final Entity entity;

    public ItemItemUI(Entity entity) {
        super();
        this.entity = entity;

        setBackground("window-bg");
        pad(10);

        setupContent();
    }

    private void setupContent() {
        // Create left column with basic info
        VisTable leftColumn = new VisTable();
        leftColumn.top();

        // Get item component
        ItemComponent itemComponent = Mappers.item.get(entity);
        String itemLabel = (itemComponent != null && itemComponent.getLabel() != null) ?
            itemComponent.getLabel() : "Unknown Item";

        // Item name (extract from label)
        String itemName = extractItemName(itemLabel);
        leftColumn.add(new VisLabel("Name: " + itemName)).left().row();

        // Full item path
        leftColumn.add(new VisLabel("Path: " + itemLabel)).left().row();

        // Entity ID
        leftColumn.add(new VisLabel("Entity ID: " + getEntityId(entity))).left().row();

        // Create right column with item details
        VisTable rightColumn = new VisTable();
        rightColumn.top();

        // Item category
        String category = extractCategory(itemLabel);
        rightColumn.add(new VisLabel("Category: " + category)).left().row();

        // Position if available
        PositionComponent positionComponent = Mappers.position.get(entity);
        if (positionComponent != null) {
            String position = String.format("(%.1f, %.1f)",
                positionComponent.getPosition().x, positionComponent.getPosition().y);
            rightColumn.add(new VisLabel("Position: " + position)).left().row();
        } else {
            rightColumn.add(new VisLabel("Position: Unknown")).left().row();
        }

        // Item component info
        if (itemComponent != null) {
            rightColumn.add(new VisLabel("Stack Size: " + itemComponent.getStackSize())).left().row();
        }

        // Add columns to main table
        add(leftColumn).expand().fill().pad(5);
        add(rightColumn).expand().fill().pad(5);
    }

    /**
     * Extract category from item label (e.g., "houses/door" -> "houses")
     */
    private String extractCategory(String itemLabel) {
        if (itemLabel == null || !itemLabel.contains("/")) {
            return "misc";
        }
        return itemLabel.substring(0, itemLabel.indexOf("/"));
    }

    /**
     * Extract item name (e.g., "houses/door" -> "door")
     */
    private String extractItemName(String itemLabel) {
        if (itemLabel == null || !itemLabel.contains("/")) {
            return itemLabel;
        }
        return itemLabel.substring(itemLabel.lastIndexOf("/") + 1);
    }

    private String getEntityId(Entity entity) {
        if (entity == null) return "Unknown";
        return String.valueOf(entity.hashCode() % 10000);
    }
}
