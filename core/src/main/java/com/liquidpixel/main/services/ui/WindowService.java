package com.liquidpixel.main.services.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.liquidpixel.main.interfaces.IGet;
import com.liquidpixel.main.interfaces.managers.IWindowManager;
import com.liquidpixel.main.interfaces.ui.IWindowService;
import com.liquidpixel.main.services.Service;

public class WindowService extends Service implements IWindowService {

    IWindowManager windowManager;

    public WindowService(IWindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public IWindowManager getWindowManager() {
        return windowManager;
    }

    public IGet<Group> getWindow(int id) {
        return windowManager.getWindow(id);
    }
}
