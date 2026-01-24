package com.liquidpixel.main.systems.render;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.BodyComponent;
import com.liquidpixel.main.components.TileSelectionComponent;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.main.utils.PhasedIteratingSystem;

public class TileSelectionRenderSystem extends PhasedIteratingSystem {

    ShapeRenderer renderer;
    SpriteBatch spriteBatch;
    OrthographicCamera camera;
    IItemService itemService;
    ISelectionService selectionService;
    IWorldMap worldMap;

    public TileSelectionRenderSystem(IItemService itemService, ISelectionService selectionService, IWorldMap worldMap) {
        super(Family.all(TileSelectionComponent.class, PositionComponent.class).get());
        this.renderer = GameResources.get().getShapeRenderer();
        this.spriteBatch = GameResources.get().getBatch();
        this.camera = GameResources.get().getCamera();
        this.itemService = itemService;
        this.selectionService = selectionService;
        this.worldMap = worldMap;
    }

    @Override
    protected void beforeFrame() {
        spriteBatch.end();
        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TileSelectionComponent tileSelectionComponent = Mappers.tileSelection.get(entity);
        PositionComponent positionComponent = Mappers.position.get(entity);
        BodyComponent bodyComponent = Mappers.body.get(entity);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setProjectionMatrix(camera.combined);
        renderer.setColor(tileSelectionComponent.getColor());

        boolean invalid = invalidTiles(tileSelectionComponent, bodyComponent, positionComponent);

        selectionService.setValidBuildingSpot(invalid);
        renderer.end();
    }

    private boolean invalidTiles(TileSelectionComponent tileSelectionComponent, BodyComponent bodyComponent, PositionComponent positionComponent) {
        int count = 0;
        if (tileSelectionComponent.isUseBodyOffset()) {
            GridPoint2 absoluteInteractionPoint = new GridPoint2(bodyComponent.getInteractionPoint()).add(positionComponent.getGridPosition());
            count += renderRect(
                tileSelectionComponent,
                positionComponent.getGridPosition(),
                bodyComponent.width,
                bodyComponent.height,
                absoluteInteractionPoint
            );
        } else {
            count += renderRect(
                tileSelectionComponent,
                positionComponent.getGridPosition(),
                tileSelectionComponent.getWidth(),
                tileSelectionComponent.getHeight());
        }
        return count == 0;
    }

    private int renderRect(TileSelectionComponent tileSelectionComponent, GridPoint2 origin, int width, int height) {
        return renderRect(
            tileSelectionComponent,
            origin,
            width,
            height,
            new GridPoint2(-200, -200)
        );
    }

    private int renderRect(TileSelectionComponent tileSelectionComponent, GridPoint2 origin, int width, int height, GridPoint2 absolutePoint) {
        float size = tileSelectionComponent.getCellSize();
        float offset = (1f - size) / 2f;
        int count = 0;

        // Adjust origin and make width/height positive if they're negative
        GridPoint2 adjustedOrigin = new GridPoint2(origin);
        int adjustedWidth = width;
        int adjustedHeight = height;

        if (width < 0) {
            adjustedOrigin.x += width + 1; // Move origin left by |width|-1 to maintain relative position
            adjustedWidth = Math.abs(width);
        }

        if (height < 0) {
            adjustedOrigin.y += height + 1; // Move origin down by |height|-1 to maintain relative position
            adjustedHeight = Math.abs(height);
        }

        for (int dx = 0; dx < adjustedWidth; dx++) {
            for (int dy = 0; dy < adjustedHeight; dy++) {
                GridPoint2 absolutePosition = new GridPoint2(adjustedOrigin.x + dx, adjustedOrigin.y + dy);

                if (worldMap.isBuildable(absolutePosition)) {
                    renderer.setColor(tileSelectionComponent.getColor());
                } else {
                    if(tileSelectionComponent.showInvalidTiles()) {
                        renderer.setColor(1, 0, 0, 0.5f);
                        count++;
                    }
                }

                if (absolutePoint.equals(absolutePosition)) {
                    renderer.setColor(0, 1, 0, 0.5f);
                }

                renderer.rect(
                    adjustedOrigin.x + dx + offset,
                    adjustedOrigin.y + dy + offset,
                    size, size
                );
            }
        }

        return count;
    }


    @Override
    protected void afterFrame() {
        Gdx.gl.glDisable(GL30.GL_BLEND);
        spriteBatch.begin();
    }

}
