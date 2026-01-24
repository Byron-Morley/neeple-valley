package com.liquidpixel.main.ui.view.common;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.liquidpixel.main.listeners.ScrollPaneListener;
import com.kotcrab.vis.ui.widget.VisScrollPane;

public class ScrollPanelUI extends VisScrollPane {

    public ScrollPanelUI(Actor widget) {
        super(widget);
        this.setScrollingDisabled(true, false);
        this.setFadeScrollBars(false);
        this.addListener(new ScrollPaneListener());
        this.pack();
    }
}
