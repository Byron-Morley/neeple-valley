package com.liquidpixel.main.ui.view.items;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.liquidpixel.main.interfaces.IGet;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.Updatable;
import com.liquidpixel.main.ui.common.ReuseableWindow;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.liquidpixel.main.engine.GameResources;
import com.badlogic.ashley.core.Family;
import com.liquidpixel.item.components.ItemComponent;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.ArrayList;
import java.util.List;

import static com.liquidpixel.main.screens.WorldScreen.UI_SCALE;

public class ItemsUI extends ReuseableWindow implements IGet<Group>, Updatable {

    IItemService itemService;
    ItemsUIPresenter presenter;

    public ItemsUI(IItemService itemService) {
        super("Items");
        this.itemService = itemService;
        setVisible(false);
        addCloseButton();
        presenter = new ItemsUIPresenter();

        setPosition(250 / UI_SCALE, 500 / UI_SCALE);
        defaults().pad(10, 5, 10, 5);
        row().fill().expandX().expandY();
        add(presenter).width(500 / UI_SCALE).height(600 / UI_SCALE);
        pack();
        updateContent();
    }

    public void updateContent() {
        // Get all entities with ItemComponent (actual items in the game world)
//        List<Entity> itemEntities = getAllItemEntities();
//        presenter.setItemEntities(itemEntities);
        pack();
    }

    private List<Entity> getAllItemEntities() {
        List<Entity> itemEntities = new ArrayList<>();

        // Get all entities that have ItemComponent
        ImmutableArray<Entity> entities = GameResources.get().getEngine()
            .getEntitiesFor(Family.all(ItemComponent.class).get());

        for (Entity entity : entities) {
            itemEntities.add(entity);
        }

        return itemEntities;
    }

    @Override
    public VisWindow get() {
        return this;
    }

    @Override
    public void update() {
        updateContent();
    }
}
