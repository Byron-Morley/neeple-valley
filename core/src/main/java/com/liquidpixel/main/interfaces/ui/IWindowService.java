package com.liquidpixel.main.interfaces.ui;

import com.liquidpixel.main.interfaces.managers.IWindowManager;

public interface IWindowService {
    IWindowManager getWindowManager();
    <T> T getWindow(int windowName);
}
