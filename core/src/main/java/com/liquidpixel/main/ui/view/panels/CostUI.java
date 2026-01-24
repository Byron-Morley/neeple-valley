package com.liquidpixel.main.ui.view.panels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.liquidpixel.main.interfaces.*;
import com.liquidpixel.main.interfaces.services.ISettlementService;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.List;

public class CostUI extends VisTable implements IGet<Group>, Updatable {

    private VisLabel titleLabel;
    private Cell<?> titleCell;
    private static final float BACKGROUND_ALPHA = 1f;
    private VisTable contentTable;
    private static final float ICON_SIZE = 18;
    ISettlementService settlementService;
    List<IStorageItem> resources;

    public CostUI(ISettlementService settlementService) {
        this.settlementService = settlementService;
        init();
    }

    public void init() {
        setVisible(false);
        pad(10);

        titleLabel = new VisLabel("");
        titleLabel.setAlignment(Align.left);
        titleCell = add(titleLabel).expandX().fillX().padBottom(5);
        this.contentTable = new VisTable();
        row();

        addSeparator().padBottom(5);
        row();
        contentTable.top().left();
        add(contentTable).top().left().expand().fill();

        setTransparentBackground();
        pack();
    }

    public void renderResourceItems() {
        contentTable.clearChildren();

        VisLabel titleLabel = new VisLabel("Materials");
        titleLabel.setFontScale(1f);
        contentTable.add(titleLabel).colspan(3).left().padBottom(5);
        contentTable.row();

        int positionInRow = 0;
        if (resources != null) {
            for (IStorageItem resource : resources) {
                VisTable itemCell = new VisTable();

//                Image icon = new Image(SpriteFactory.getSprite(resource.getSpriteName()));
                Image icon = null;
                if (icon != null) {
                    icon.setSize(ICON_SIZE, ICON_SIZE);

                    VisLabel quantityLabel = new VisLabel("" + resource.getQuantity());
                    quantityLabel.setFontScale(1.1f);

                    int inStock = settlementService.getResourceQuantity(resource.getName());
                    VisLabel inventoryLabel = new VisLabel("(" + inStock + ")");
                    inventoryLabel.setFontScale(1.0f);

                    itemCell.add(icon).size(ICON_SIZE);
                    itemCell.add(quantityLabel).padLeft(10).left();
                    itemCell.add(inventoryLabel).padLeft(5).left();

                    if (resource.getQuantity() > inStock) {
                        icon.setColor(icon.getColor().r, icon.getColor().g, icon.getColor().b, 0.5f);
                        quantityLabel.setColor(0.6f, 0.6f, 0.6f, 0.7f);
                    }

                    contentTable.add(itemCell).left().padRight(15);
                    positionInRow++;

                    if (positionInRow >= 3) {
                        contentTable.row();
                        positionInRow = 0;
                    }
                }
            }

            if (positionInRow > 0 && positionInRow < 3) {
                for (int i = positionInRow; i < 3; i++) {
                    contentTable.add().left();
                }
                contentTable.row();
            }
            pack();
        }
    }

    public void addResourceItems(List<IStorageItem> resources) {
        this.resources = resources;
        renderResourceItems();
    }

    public void pack() {
        super.pack();
        setWidth(240);
    }

    public void reset() {
        resources.clear();
        contentTable.clearChildren();
        setTitle("");
        setVisible(false);
    }


    private void setTransparentBackground() {
        Drawable background = VisUI.getSkin().getDrawable("window");

        if (background != null) {
            setBackground(background);
            setColor(37, 37, 38, BACKGROUND_ALPHA);
        } else {
            setColor(new Color(37, 37, 38, BACKGROUND_ALPHA));
        }
    }

    public void setTitle(String newTitle) {
        titleLabel.setText(newTitle);
    }

    @Override
    public Group get() {
        return this;
    }

    @Override
    public void update() {
        renderResourceItems();
    }
}
