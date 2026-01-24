package com.liquidpixel.main.managers;

import com.liquidpixel.main.interfaces.*;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.main.interfaces.ui.IWindowService;
import com.liquidpixel.main.listeners.build.ObstacleBuildListener;
import com.liquidpixel.main.listeners.item.*;
import com.liquidpixel.main.listeners.spawnMenu.*;
import com.liquidpixel.main.model.item.ClickBehaviorType;
import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.selection.api.IClickBehavior;
import com.liquidpixel.selection.api.IClickBehaviorManager;
import com.liquidpixel.selection.api.IClickBehaviorService;
import com.liquidpixel.selection.api.IMenuClickBehavior;
import com.liquidpixel.selection.services.ClickBehaviorService;

public class ClickBehaviorManager implements IClickBehaviorManager {

    IItemService itemService;
    IMapService mapService;
    ICameraService cameraService;
    IWorldMap worldMap;
    IWindowService windowService;
    ISelectionService selectionService;
    ISettlementService settlementService;
    IClickBehaviorService clickBehaviorService;
    ObstacleBuildListener obstacleBuildListener;

    public ClickBehaviorManager() {
        clickBehaviorService = new ClickBehaviorService(this);
    }

    public void init(IItemService itemService, IMapService mapService, ICameraService cameraService, IWorldMap worldMap, IWindowService windowService, ISelectionService selectionService, ISettlementService settlementService,  ObstacleBuildListener obstacleBuildListener) {
        this.itemService = itemService;
        this.mapService = mapService;
        this.cameraService = cameraService;
        this.worldMap = worldMap;
        this.windowService = windowService;
        this.selectionService = selectionService;
        this.settlementService = settlementService;
        this.obstacleBuildListener = obstacleBuildListener;
        this.worldMap = mapService.getWorldMap();
    }

    /**
     * This is for adding listeners to new items
     *
     *
     * @param behaviorId
     * @return
     */
    public IClickBehavior createClickBehavior(String behaviorId) {
        ClickBehaviorType type = ClickBehaviorType.fromString(behaviorId);

        return switch (type) {
            case STORAGE_INFO -> new StorageClickBehavior(windowService);
            case SETTLEMENT_DETAILS -> new SettlementClickBehavior(selectionService);
            case RESOURCE_INFO -> new ResourceClickBehavior();
            case FARM_WINDOW -> new FarmClickBehavior(windowService);
            case UNIVERSAL_WINDOW -> new WindowClickBehavior(windowService, itemService);
            default -> null;
        };
    }

    /**
     * This is for adding listeners to menu items
     *
     * @param behaviorId
     * @return
     */
    public IMenuClickBehavior createMenuClickBehavior(String behaviorId) {
        ClickBehaviorType type = ClickBehaviorType.fromString(behaviorId);
        return switch (type) {
            case DESTROY -> new DestroyClickListener(selectionService, cameraService, itemService, worldMap, obstacleBuildListener);
            case HARVEST -> new HarvestClickListener(settlementService, selectionService, cameraService, itemService, worldMap);
            case SPAWN -> new SpawnClickListener(settlementService, selectionService, cameraService, itemService);
            case AREA -> new AreaClickListener(settlementService, selectionService, cameraService, itemService, worldMap);
            case PAINT -> new PaintTerrainClickListener(selectionService, itemService);

            default -> null;
        };
    }

    @Override
    public IClickBehaviorService getClickBehaviorService() {
        return clickBehaviorService;
    }
}
