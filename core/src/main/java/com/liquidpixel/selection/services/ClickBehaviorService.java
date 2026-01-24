package com.liquidpixel.selection.services;

import com.liquidpixel.selection.api.IClickBehavior;
import com.liquidpixel.selection.api.IClickBehaviorService;
import com.liquidpixel.selection.api.IMenuClickBehavior;
import com.liquidpixel.main.managers.ClickBehaviorManager;

public class ClickBehaviorService implements IClickBehaviorService {

    ClickBehaviorManager clickBehaviorManager;

    public ClickBehaviorService(ClickBehaviorManager clickBehaviorManager) {
        this.clickBehaviorManager = clickBehaviorManager;
    }

    @Override
    public IClickBehavior createClickBehavior(String behaviorId) {
        return clickBehaviorManager.createClickBehavior(behaviorId);
    }

    @Override
    public IMenuClickBehavior createMenuClickBehavior(String behaviorId) {
        return clickBehaviorManager.createMenuClickBehavior(behaviorId);
    }
}
