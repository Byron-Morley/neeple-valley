package com.liquidpixel.main.ui.view.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.liquidpixel.main.interfaces.*;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.interfaces.services.ISettlementService;
import com.liquidpixel.main.interfaces.ui.IResizePosition;
import com.liquidpixel.main.utils.IntervalTimer;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

import java.util.List;

import static com.liquidpixel.main.screens.WorldScreen.UI_SCALE;

public class ResourcesUI extends VisWindow implements IGet<Group>, Updatable, IResizePosition {

    final IntervalTimer updateTimer;
    IItemService itemService;
    ISelectionService selectionService;
    ISettlementService settlementService;
    private VisTable contentTable;
    private static final float ICON_SIZE = 18;

    public ResourcesUI(ISelectionService selectionService, IItemService itemService, ISettlementService settlementService) {
        super("Resources");
        this.itemService = itemService;
        this.selectionService = selectionService;
        this.settlementService = settlementService;
        this.updateTimer = new IntervalTimer(1000);
        this.contentTable = new VisTable();
        init();
    }

    public void init() {
        setMovable(false);
        setResizable(false);
        defaults().pad(5 / UI_SCALE, 5 / UI_SCALE, 5 / UI_SCALE, 5 / UI_SCALE);

        contentTable.top().left();
        add(contentTable).top().left().expand().fill();

        updateContent();
        updatePosition();
        pack();
    }

    private void updatePosition() {
        setPosition(10, Gdx.graphics.getHeight() - getHeight() - 10);
    }

    private void addResourceRow(IStorageItem resource) {
        VisTable row = new VisTable();
//        Image icon = new Image(SpriteFactory.getSprite(resource.getSpriteName()));
        Image icon = null;
        icon.setSize(ICON_SIZE, ICON_SIZE);

        VisLabel quantityLabel = new VisLabel("" + resource.getQuantity());
        quantityLabel.setFontScale(1.1f);

        row.add(icon).size(ICON_SIZE);
        row.add(quantityLabel).padLeft(10).left();

        contentTable.add(row).left();
        contentTable.row();
    }

    private void updateContent() {

        List<IStorageItem> colonialResources = itemService.getColonyComponents();

        for (IStorageItem resource : colonialResources) {
            addResourceRow(resource);
        }

        List<IStorageItem> resources = settlementService.getSettlementResources();
        for (IStorageItem resource : resources) {
            addResourceRow(resource);
        }
    }

    public void update() {
        if (updateTimer.isReady()) {
            contentTable.clearChildren();
            updateContent();
            pack();
        }
    }

    @Override
    public VisWindow get() {
        return this;
    }

    @Override
    public void resize() {
        updatePosition();
    }
}
