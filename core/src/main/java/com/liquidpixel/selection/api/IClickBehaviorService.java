package com.liquidpixel.selection.api;

public interface IClickBehaviorService {
    IClickBehavior createClickBehavior(String behaviorId);

    IMenuClickBehavior createMenuClickBehavior(String behaviorId);
}
