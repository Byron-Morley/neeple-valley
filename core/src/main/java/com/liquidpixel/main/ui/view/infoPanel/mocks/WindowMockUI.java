package com.liquidpixel.main.ui.view.infoPanel.mocks;

import com.liquidpixel.main.interfaces.Updatable;
import com.liquidpixel.main.ui.UiTestScreen;
import com.kotcrab.vis.ui.widget.VisTable;

public class WindowMockUI implements UiTestScreen.TestUITab, Updatable {
   WindowListUI panel;

    @Override
    public String getTabTitle() {
        return "Window UI Tab";
    }

    @Override
    public void setupUI(VisTable contentTable) {
        panel = new WindowListUI();
        contentTable.add(panel);
    }

    @Override
    public void update() {
        if (panel != null) {
            panel.update();
        }
    }

}
