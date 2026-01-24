package com.liquidpixel.main.ui.view.infoPanel.mocks.tabs;

import com.badlogic.gdx.graphics.Color;
import com.liquidpixel.main.ui.view.interfaces.IUniversalTab;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Simple Info tab that displays basic information about an entity
 */
public class InfoTabExample implements IUniversalTab {

    private final VisTable contentTable;
    private VisLabel nameLabel;
    private VisLabel typeLabel;
    private VisLabel statusLabel;
    private VisLabel descriptionLabel;

    public InfoTabExample() {
        contentTable = new VisTable();
        setupUI();
    }

    private void setupUI() {
        contentTable.top().left();
        contentTable.pad(10);

        // Create labels
        nameLabel = new VisLabel("Entity Name: Building #1");
        nameLabel.setFontScale(1.2f);
        nameLabel.setColor(Color.WHITE);

        typeLabel = new VisLabel("Type: Farm House");
        typeLabel.setColor(Color.LIGHT_GRAY);

        statusLabel = new VisLabel("Status: Active");
        statusLabel.setColor(Color.GREEN);

        descriptionLabel = new VisLabel("A simple farm house that produces\nfood and houses workers.");
        descriptionLabel.setColor(Color.GRAY);
        descriptionLabel.setWrap(true);

        // Layout
        contentTable.add(nameLabel).expandX().left().padBottom(10).row();
        contentTable.add(typeLabel).expandX().left().padBottom(5).row();
        contentTable.add(statusLabel).expandX().left().padBottom(10).row();
        contentTable.add(new VisLabel("Description:")).expandX().left().padBottom(5).row();
        contentTable.add(descriptionLabel).expandX().left().width(400).padBottom(10).row();

        // Add some stats
        VisTable statsTable = new VisTable();
        statsTable.add(new VisLabel("Health:")).left().padRight(10);
        statsTable.add(new VisLabel("100/100")).left().expandX().row();
        statsTable.add(new VisLabel("Level:")).left().padRight(10);
        statsTable.add(new VisLabel("3")).left().expandX().row();
        statsTable.add(new VisLabel("Efficiency:")).left().padRight(10);
        statsTable.add(new VisLabel("85%")).left().expandX().row();

        contentTable.add(new VisLabel("Stats:")).expandX().left().padBottom(5).row();
        contentTable.add(statsTable).expandX().left().padBottom(10).row();
    }

    @Override
    public String getTabTitle() {
        return "Info";
    }

    @Override
    public VisTable getContentTable() {
        return contentTable;
    }

    @Override
    public void updateContent() {
        // In a real implementation, this would update with actual entity data
        // For now, just simulate some changing data
        statusLabel.setText("Status: " + (Math.random() > 0.5 ? "Active" : "Idle"));
        statusLabel.setColor(statusLabel.getText().toString().contains("Active") ? Color.GREEN : Color.YELLOW);
    }
}
