package com.liquidpixel.main.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.pathfinding.components.MovementTaskComponent;
import com.liquidpixel.selection.api.IClickAction;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.managers.ComponentManager;

import java.util.ArrayList;
import java.util.List;

public class MoveClickAction implements IClickAction {

    List<Entity> entities;
    ISelectionService selectionService;
    IItemService itemService;
    IMapService mapService;
    List<Entity> markers = new ArrayList<>();

    public MoveClickAction(List<Entity> entities, ISelectionService selectionService,
                          IItemService itemService, IMapService mapService) {
        this.entities = entities;
        this.selectionService = selectionService;
        this.itemService = itemService;
        this.mapService = mapService;
    }

    // Constructor overload for backward compatibility
    public MoveClickAction(Entity entity, ISelectionService selectionService,
                          IItemService itemService, IMapService mapService) {
        this.entities = new ArrayList<>();
        if (entity != null) {
            this.entities.add(entity);
        }
        this.selectionService = selectionService;
        this.itemService = itemService;
        this.mapService = mapService;
    }

    @Override
    public IClickAction execute(GridPoint2 position) {
        List<GridPoint2> availablePositions = findAvailablePositions(position, entities.size());

        for (int i = 0; i < entities.size(); i++) {
            if (i < availablePositions.size()) {
                GridPoint2 targetPosition = availablePositions.get(i);
                Entity marker = itemService.getItem("ui/circle").build();
                itemService.spawnItem(marker, targetPosition);
                markers.add(marker);

                Entity entity = entities.get(i);
                ComponentManager.replace(entity, new MovementTaskComponent(targetPosition, marker), MovementTaskComponent.class);
            }
        }

        return this;
    }

    private List<GridPoint2> findAvailablePositions(GridPoint2 center, int count) {
        List<GridPoint2> available = new ArrayList<>();
        available.add(center); // Add the clicked position first

        // If we need more positions, spiral outward from the center
        if (count > 1) {
            int radius = 1;
            while (available.size() < count) {
                // Check surrounding tiles in a spiral pattern
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dy = -radius; dy <= radius; dy++) {
                        // Skip positions that are not on the current ring
                        if (Math.abs(dx) != radius && Math.abs(dy) != radius) {
                            continue;
                        }

                        GridPoint2 pos = new GridPoint2(center.x + dx, center.y + dy);

                        // Check if the position is valid and not already in our list
                        if (isPositionAvailable(pos) && !containsPosition(available, pos)) {
                            available.add(pos);
                            if (available.size() >= count) {
                                return available;
                            }
                        }
                    }
                }
                radius++;
            }
        }

        return available;
    }

    private boolean isPositionAvailable(GridPoint2 position) {
        // Check if the position is valid (e.g., within map bounds and walkable)
        return mapService.isWalkableNode(position);
    }

    private boolean containsPosition(List<GridPoint2> positions, GridPoint2 position) {
        for (GridPoint2 pos : positions) {
            if (pos.x == position.x && pos.y == position.y) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isOneShot() {
        return false;
    }

    @Override
    public IClickAction exit() {
        // Dispose all markers
        for (Entity marker : markers) {
            itemService.dispose(marker);
        }
        markers.clear();
        selectionService.clearSelected();
        return null;
    }
}
