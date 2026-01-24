package com.liquidpixel.main.ui.view.toolbelt;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.liquidpixel.main.interfaces.IGet;

import java.util.List;

public class ToolbeltUI implements IGet<Group> {
    ToolbeltContainer container;

    public ToolbeltUI(Skin skin) {
        container = new ToolbeltContainer(skin);
    }

    public List<ToolbeltSlot> getSlots() {
        return container.getSlots();
    }



    @Override
    public Group get() {
        return this.container;
    }
}
