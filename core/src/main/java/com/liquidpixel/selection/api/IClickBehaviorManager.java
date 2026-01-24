package com.liquidpixel.selection.api;

import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.main.interfaces.ui.IWindowService;
import com.liquidpixel.main.listeners.build.ObstacleBuildListener;
import com.liquidpixel.pathfinding.api.IMapService;

public interface IClickBehaviorManager {
    IClickBehaviorService getClickBehaviorService();

    IClickBehavior createClickBehavior(String behaviorId);

    IMenuClickBehavior createMenuClickBehavior(String behaviorId);
    void init(IItemService itemService, IMapService mapService, ICameraService cameraService, IWorldMap worldMap, IWindowService windowService, ISelectionService selectionService, ISettlementService settlementService, ObstacleBuildListener obstacleBuildListener);
}
