package com.liquidpixel.main.ui.view.infoPanel.mocks;

import com.liquidpixel.main.ui.common.ReuseableWindow;

public class WindowTest extends ReuseableWindow {
    public WindowTest() {
        super("window test");
        setVisible(true);
        addCloseButton();
        setSize(170, 200);
        setPosition(0, 0);
        defaults().pad(10, 5, 10, 5);
        row().fill().expandX().expandY();
        pack();
    }
}
