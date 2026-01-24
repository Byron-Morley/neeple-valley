package com.liquidpixel.main.ui.view.infoPanel.mocks;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.liquidpixel.main.interfaces.IGet;
import com.liquidpixel.main.interfaces.Updatable;
import com.liquidpixel.main.ui.UiTestScreen;
import com.liquidpixel.main.ui.common.ReuseableWindow;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.liquidpixel.main.screens.WorldScreen.UI_SCALE;

public class WindowListUI extends ReuseableWindow implements Updatable {

    WindowListUIPresenter presenter;

    public WindowListUI() {
        super("Window List");
        setVisible(true);
        addCloseButton();
        presenter = new WindowListUIPresenter();

        setPosition(250, 500);
        row().fill().expandX().expandY();
        add(presenter).width(500).height(600);
        pack();
        updateContent();
    }

    public void updateContent() {
        Map<WindowCategory, List<String>> itemsByCategory = new HashMap<>();

        // Sample data - categories with items
        itemsByCategory.put(WindowCategory.FARMING, Arrays.asList("Wheat", "Corn", "Tomatoes", "Carrots", "Potatoes"));
        itemsByCategory.put(WindowCategory.CRAFTING, Arrays.asList("Hammer", "Saw", "Nails", "Wood Planks", "Steel Bars"));
        itemsByCategory.put(WindowCategory.TRADING, Arrays.asList("Gold Coins", "Trade Routes", "Merchant Ships", "Market Stalls"));
        itemsByCategory.put(WindowCategory.STORAGE, Arrays.asList("Warehouse", "Chest", "Barrel", "Crate", "Storage Room"));

        presenter.setItems(itemsByCategory);
        pack();
    }

    @Override
    public void update() {
        updateContent();
    }

    public void setupUI(VisTable table) {
        table.add(this).expand().fill();
        setVisible(true);
    }

    public enum WindowCategory {
        FARMING,
        CRAFTING,
        TRADING,
        STORAGE
    }
}
