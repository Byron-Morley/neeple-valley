package com.liquidpixel.main.ui.view.tabs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.liquidpixel.main.components.items.FarmComponent;
import com.liquidpixel.main.components.items.GrowableComponent;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.main.engine.GameClock;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.ui.IWindowService;
import com.liquidpixel.main.listeners.farming.RemoveCropButtonListener;
import com.liquidpixel.main.ui.model.farming.FarmItem;
import com.liquidpixel.main.ui.view.interfaces.IUniversalTab;
import com.liquidpixel.main.ui.view.listItems.FarmItemUI;
import com.liquidpixel.main.utils.Farmables;
import com.liquidpixel.main.utils.Mappers;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.List;

public class FarmTab implements IUniversalTab {

    private final VisTable contentTable;
    private final VisTable farmItemListTable;
    private VisScrollPane scrollPane;

    IItemService itemService;
    IWindowService windowService;

    Entity farm;

    private VisTextButton plantStopButton;
    private VisTextButton loopQueueButton;

    private int lastUpdateHour = -1;

    public FarmTab(Entity farm, IItemService itemService, IWindowService windowService) {
        this.farm = farm;
        this.windowService = windowService;
        this.itemService = itemService;
        this.contentTable = new VisTable();
        farmItemListTable = new VisTable();
        init();
    }

    private void init() {
        contentTable.top().left();
        contentTable.pad(5);

        VisTable addSection = buildHeader();
        contentTable.add(addSection).left().padBottom(10).row();

        // Create scroll pane for farm items like WorkOrderTabContent
        farmItemListTable.top();

        // Create scroll pane
        scrollPane = new VisScrollPane(farmItemListTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);

        contentTable.add(scrollPane).grow().minHeight(350).top().row();
        update();
    }

    private VisTable buildHeader() {
        VisTable addSection = new VisTable();

        // Create select box with farmable items
        VisSelectBox<String> farmableSelectBox = new VisSelectBox<>();
        farmableSelectBox.setItems(Farmables.FARMABLE_ITEMS.toArray(new String[0]));
        farmableSelectBox.setSelected(Farmables.FARMABLE_ITEMS.get(0)); // Select first item by default

        // Add button to add selected item
        VisTextButton addButton = new VisTextButton("Add");
        addButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String selectedItem = farmableSelectBox.getSelected();
                FarmComponent farmComponent = Mappers.farm.get(farm);
                Entity crop = itemService.getItem(selectedItem).build();
                farmComponent.addCropToPlant(crop);

                update();
            }
        });

        addSection.add(farmableSelectBox).padRight(10);
        addSection.add(addButton).padRight(20);
        return addSection;
    }

    private void update() {
        farmItemListTable.clear();
        farmItemListTable.top();

        FarmComponent farmComponent = Mappers.farm.get(farm);

        List<Entity> cropsTList = farmComponent.getCropsToPlant();
        if (cropsTList.size() > 0) addCrop(cropsTList.get(0), farmComponent);


        // Force layout updates
        farmItemListTable.invalidateHierarchy();
        scrollPane.invalidateHierarchy();
        contentTable.invalidateHierarchy();

        farmItemListTable.validate();
        scrollPane.validate();
        contentTable.validate();

    }

    private void addCrop(Entity crop, FarmComponent farmComponent) {
        GrowableComponent growableComponent = Mappers.growable.get(crop);
        GameClock.DayHour result = GameClock.convertHoursToDayHour(growableComponent.getHoursRemainingToGrow());

        FarmItem farmItem = new FarmItem(itemService.getStorageItem(crop), result.days(), result.hours());

        FarmItemUI farmItemUI = new FarmItemUI(
            farmItem,
            farmComponent,
            new RemoveCropButtonListener(farm, itemService, windowService, this::update)
        );

        farmItemListTable.add(farmItemUI).growX().height(120).padBottom(5).top().row();
    }

    @Override
    public void updateContent() {
        int currentHour = GameClock.getHours();
        int currentDay = GameClock.getDays();

        // Create a unique identifier for the current hour that accounts for day changes
        int currentHourId = (currentDay - 1) * 24 + currentHour;

        // Only update if we're in a different hour than the last update
        if (currentHourId != lastUpdateHour) {
            lastUpdateHour = currentHourId;
            update();
        }
    }

    @Override
    public String getTabTitle() {
        return "Farming";
    }

    @Override
    public VisTable getContentTable() {
        return contentTable;
    }
}
