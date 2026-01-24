package com.liquidpixel.main.systems.spectral;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.actions.MoveClickAction;
import com.liquidpixel.main.ai.pathfinding.FlatTiledNode;
import com.liquidpixel.main.components.ClickableComponent;
import com.liquidpixel.main.components.ShapeComponent;
import com.liquidpixel.main.components.render.RenderComponent;
import com.liquidpixel.main.components.selection.SelectableEntityComponent;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.main.interfaces.ui.IUIService;
import com.liquidpixel.main.interfaces.ui.IWindowService;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.main.utils.events.Messages;
import com.liquidpixel.main.utils.shape.RectangleShape;
import com.liquidpixel.pathfinding.api.IMapService;

import java.util.ArrayList;
import java.util.List;

import static com.liquidpixel.main.managers.EntityIdManager.getEntityName;


public class SpectralSelectionSystem extends EntitySystem {

    ImmutableArray<Entity> selectableEntities;
    Vector2 clickedPosition;
    GridPoint2 currentTile;
    boolean startedPotentialDrag = false;
    boolean isDragging = false;
    boolean isDraggingAllowed = true;
    Vector2 dragStartPosition = new Vector2();
    Vector2 dragCurrentPosition = new Vector2();
    Entity selectionBox;

    ICameraService cameraService;
    IItemService itemService;
    IMapService mapService;
    IUIService uiService;
    ISelectionService selectionService;
    IPlayerInputService playerInputService;
    IWindowService windowService;

    public SpectralSelectionSystem(
        IPlayerInputService playerInputService,
        ICameraService cameraService,
        IUIService uiService,
        ISelectionService selectionService,
        IItemService itemService,
        IMapService mapService,
        IWindowService windowService) {
        this.cameraService = cameraService;
        this.itemService = itemService;
        this.mapService = mapService;
        this.uiService = uiService;
        this.selectionService = selectionService;
        this.playerInputService = playerInputService;
        this.windowService = windowService;
    }

    @Override
    public void addedToEngine(Engine engine) {
        selectableEntities = engine.getEntitiesFor(Family.all(SelectableEntityComponent.class, PositionComponent.class, RenderComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {

        handleHoverInformation();


        if (playerInputService.isLeftClicking() && playerInputService.isLeftClickAllowed() && !isDragging) {
            handleLeftClick();
        }

        if (playerInputService.isRightClicking()) {
            handleRightClick();
        }

        if (playerInputService.isLeftClicking() && !isDragging && !uiService.isMouseOverUI() && !selectionService.hasLeftClickActionQueued()) {
            if (isDraggingAllowed && hasStartedDragging()) {
                startDragging();
            }
        }

        if (isDraggingAllowed && isDragging) {
            updateDragging();

            if (!Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
                handleDragComplete();
                startedPotentialDrag = false;
                isDragging = false;
            }
        }

        if (startedPotentialDrag && !Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
            startedPotentialDrag = false;
            isDragging = false;
        }
    }

    private void updateDragging() {
        Vector2 unprojectedCursorPosition = cameraService.getUnprojectedCursorPosition();
        dragCurrentPosition.set(unprojectedCursorPosition.x, unprojectedCursorPosition.y);

        if (selectionBox != null && Mappers.shape.has(selectionBox)) {
            RectangleShape shape = (RectangleShape) Mappers.shape.get(selectionBox).getShape();
            shape.setWidth(dragCurrentPosition.x - dragStartPosition.x);
            shape.setHeight(dragCurrentPosition.y - dragStartPosition.y);
        }
    }

    private boolean hasStartedDragging() {
        Vector2 clickPosition = cameraService.getUnprojectedCursorPosition();

        if (!startedPotentialDrag) {
            dragStartPosition.set(clickPosition.x, clickPosition.y);
            startedPotentialDrag = true;
        }

        if (Math.abs(clickPosition.x - dragStartPosition.x) > 0.1f || Math.abs(clickPosition.y - dragStartPosition.y) > 0.1f) {
            isDragging = true;
            return true;
        }

        return false;
    }

    private void startDragging() {
        selectionBox = new Entity();
        RectangleShape shape = new RectangleShape(0, 0, dragStartPosition.x, dragStartPosition.y);
        shape.setColor(new Color(1, 1, 1, 0.5f));
        selectionBox.add(new ShapeComponent(shape, 5f));
        selectionBox.add(new PositionComponent(dragStartPosition.x, dragStartPosition.y));
        getEngine().addEntity(selectionBox);
    }

    private void handleHoverInformation() {
        GridPoint2 cursorLocation = cameraService.getCursorGridLocation();

        if (cursorLocation != null && cursorLocation != currentTile) {
            currentTile = cursorLocation;
            MessageManager.getInstance().dispatchMessage(Messages.CURSOR_TILE_POSITION, cursorLocation);
            FlatTiledNode node = mapService.getWorldMap().getNode(cursorLocation);

            if (node != null) {
                MessageManager.getInstance().dispatchMessage(Messages.IS_WALKABLE, "Is Walkable?: " + node.isWalkable());
                MessageManager.getInstance().dispatchMessage(Messages.CONNECTION_COUNT, "Connections: " + node.getConnections().size);
                Entity entity = mapService.getWorldMap().getEntityAtPosition(cursorLocation);

                if (entity == null) {
                    MessageManager.getInstance().dispatchMessage(Messages.HOVER_ENTITY, "Entity: " + "null");
                } else {
                    MessageManager.getInstance().dispatchMessage(Messages.HOVER_ENTITY, "Entity: " + getEntityName(entity));
                }
            }
        }
    }

    private void handleDragComplete() {
        if (selectionBox != null) {
            getEngine().removeEntity(selectionBox);
            selectionBox = null;
        }

        List<GridPoint2> positions = getSelectionBoxPositions(dragStartPosition, dragCurrentPosition);

        List<Entity> entities = new ArrayList<>();
        for (GridPoint2 position : positions) {
            Entity entity = mapService.getWorldMap().getSelectableEntityAtPosition(position);

            if (entity != null) {
                entities.add(entity);
                selectionService.addSelectedEntity(entity);
            }
        }

        List<Entity> selectableAgents = entities.stream().filter(Mappers.agent::has).filter(Mappers.selection::has).toList();

        if (!selectableAgents.isEmpty()) {
            selectionService.addLeftClickAction(new MoveClickAction(selectableAgents, selectionService, itemService, mapService));
            isDraggingAllowed = false;
        }

    }

    private List<GridPoint2> getSelectionBoxPositions(Vector2 start, Vector2 end) {
        List<GridPoint2> positions = new ArrayList<>();

        // Find the min and max coordinates to create the bounds of the rectangle
        int minX = (int) Math.floor(Math.min(start.x, end.x));
        int maxX = (int) Math.floor(Math.max(start.x, end.x));
        int minY = (int) Math.floor(Math.min(start.y, end.y));
        int maxY = (int) Math.floor(Math.max(start.y, end.y));

        // Iterate through all grid points within the bounds
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                positions.add(new GridPoint2(x, y));
            }
        }

        return positions;
    }

    private void handleRightClick() {
        if (!uiService.isMouseOverUI()) {
            boolean actionQueued = selectionService.hasLeftClickActionQueued();
            boolean isNewClick = playerInputService.isNewRightClick();
            if (isNewClick) {
                if (actionQueued) {
                    Gdx.input.setCursorCatched(false);
                    selectionService.cancelLeftClickAction();
                } else {
                    selectionService.clearSelected();
                }
            }
        }
    }

    private void handleLeftClick() {

        if (!uiService.isMouseOverUI()) {
            GridPoint2 cursorPosition = cameraService.getCursorGridLocation();
            attemptToClickThisPosition(cursorPosition);
        }
    }

    private void attemptToClickThisPosition(GridPoint2 cursorPosition) {
        boolean isNewClick = playerInputService.isNewLeftClick();
        clickedPosition = cameraService.getCursorLocation();
        GridPoint2 position = new GridPoint2((int) Math.floor(clickedPosition.x), (int) Math.floor(clickedPosition.y));
        Entity entity = mapService.getWorldMap().getSelectableEntityAtPosition(position);
        // Try to execute a queued left-click action first

        boolean actionExecuted = false;

        if (entity == null) {
            actionExecuted = selectionService.executeNextLeftClickAction(cursorPosition, isNewClick);
        }

        // If no action was executed, continue with normal selection logic
        if (!actionExecuted) {
            isDraggingAllowed = true;

            boolean clickedElsewhere = true;
            clickedPosition = cameraService.getCursorLocation();

            if (selectionService.isSelectionAllowed()) {
                if (entity != null) {
                    ClickableComponent clickable = Mappers.clickable.get(entity);
                    if (clickable != null) {
                        clickable.handleClick(entity);
                    }

                    selectSelectableItem(entity);

                    clickedElsewhere = false;
                    System.out.println("Clicked on entity");
                }
            } else {
                Gdx.app.log("SpectralSelectionSystem", "Selection not allowed");
            }

            // ClickedElsewhere = not clicked on another entity
            if (clickedElsewhere && !uiService.isMouseOverUI()) {
                if (entity != null) {
                    selectionService.addSelectedEntity(entity);
                }
            }
        } else {
//                Gdx.input.setCursorCatched(true);
        }

    }

    private void selectSelectableItem(Entity entity) {
        selectionService.clearSelected(entity);
        selectionService.addSelectedEntity(entity);

        //TODO this is inefficient, we need to embed the selection function into the yaml
        if (Mappers.selection.has(entity) && Mappers.agent.has(entity)) {
            selectionService.addLeftClickAction(new MoveClickAction(entity, selectionService, itemService, mapService));
        }

        isDraggingAllowed = false;
    }
}
