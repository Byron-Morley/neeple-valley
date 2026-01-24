package com.liquidpixel.main.interfaces.managers;

import com.liquidpixel.main.services.ui.UIService;
import com.liquidpixel.main.ui.view.debug.DebugOverlayManager;

public interface IUIManager {
    DebugOverlayManager getDebugOverlayManager();
    UIService getUiService();
}
