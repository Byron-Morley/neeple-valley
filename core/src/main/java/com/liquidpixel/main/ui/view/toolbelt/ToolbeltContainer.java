package com.liquidpixel.main.ui.view.toolbelt;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;

import static com.liquidpixel.main.utils.PlayerUtils.INVENTORY_LIMIT;

public class ToolbeltContainer extends Table {
    private List<ToolbeltSlot> slots;
    public ToolbeltContainer(Skin skin) {
        super(skin);
        setFillParent(true);
        align(Align.top);
        Table container = new Table(skin);
        add(container);
        container.align(Align.top);
        container.setBackground("panel-brown");

        slots = new ArrayList<>();

        for (int i = 0; i < INVENTORY_LIMIT; i++) {
            ToolbeltSlot slot = new ToolbeltSlot(skin);
            slots.add(slot);
            container.add(slot);
        }
    }

    public List<ToolbeltSlot> getSlots() {
        return slots;
    }

    public void setSlots(List<ToolbeltSlot> slots) {
        this.slots = slots;
    }
}
