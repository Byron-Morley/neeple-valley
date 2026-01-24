package com.liquidpixel.main.ui.view.infoPanel.tabs;

import com.liquidpixel.main.interfaces.Updatable;
import com.liquidpixel.main.ui.UiTestScreen;
import com.liquidpixel.main.ui.view.infoPanel.mocks.InfoPanelUIMock;
import com.kotcrab.vis.ui.widget.VisTable;

public class InfoTab implements UiTestScreen.TestUITab, Updatable {


    InfoPanelUIMock infoPanelUIMock;

    @Override
    public String getTabTitle() {
        return "InfoTab";
    }

    @Override
    public void setupUI(VisTable contentTable) {
        infoPanelUIMock = new InfoPanelUIMock();
        contentTable.add(infoPanelUIMock);
    }

    @Override
    public void update() {
        if (infoPanelUIMock != null) {
            infoPanelUIMock.update();
        }
    }
}
