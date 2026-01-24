package com.liquidpixel.main.systems.render;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.managers.EntityInteractionManager;
import com.liquidpixel.sprite.model.GameSprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.components.BodyComponent;
import com.liquidpixel.main.components.render.RenderComponent;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.utils.Dimensions;
import com.liquidpixel.main.utils.Mappers;

import java.util.Comparator;
import java.util.List;

public class BatchedRenderSystem extends SortedIteratingSystem {
    private ComponentMapper<PositionComponent> pm = Mappers.position;
    private ComponentMapper<RenderComponent> rm = Mappers.render;
    private SpriteBatch batch;

    public BatchedRenderSystem() {
        super(Family.all(PositionComponent.class, RenderComponent.class).get(), new ZComparator());
        this.batch = GameResources.get().getBatch();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent positionComponent = pm.get(entity);
        RenderComponent renderComponent = rm.get(entity);
        List<GameSprite> sprites = renderComponent.getSprites();
        OrthographicCamera camera = GameResources.get().getCamera();

        if (sprites.isEmpty()) {
            Gdx.app.debug("BatchedRenderSystem", "No sprites found for entity!");
            return;
        }

        for (GameSprite sprite : sprites) {
            Vector2 pos = renderComponent.getRenderPositionStrategy().process(
                positionComponent.getX(), positionComponent.getY());

            float x = pos.x - Dimensions.toMeters(sprite.getOriginX());
            float y = pos.y - Dimensions.toMeters(sprite.getOriginY());
            float width = Dimensions.toMeters(sprite.getRegionWidth());
            float height = Dimensions.toMeters(sprite.getRegionHeight());

            // Check if sprite has valid texture
            if (sprite.getTexture() == null) {
                Gdx.app.error("BatchedRenderSystem", "Sprite has NULL texture!");
                continue;
            }

            // Frustum culling check
            float margin = 2.5f;
            boolean inFrustum = camera.frustum.boundsInFrustum(
                x - width * margin, y - height * margin, 0,
                width * (margin * 2), height * (margin * 2), 0);

            batch.setColor(renderComponent.getColor());
            batch.draw(sprite, x, y,
                sprite.getOriginX(), sprite.getOriginY(),
                width, height,
                sprite.getScaleX(), sprite.getScaleY(),
                sprite.getRotation()
            );
        }
    }


    /**
     * This ZComparator can be really expensive if there are too many render entities
     **/
    private static class ZComparator implements Comparator<Entity> {
        private static final int DIFF_MULTIPLIER = 10;

        private ComponentMapper<PositionComponent> pm = Mappers.position;
        private ComponentMapper<RenderComponent> rm = Mappers.render;
        private ComponentMapper<BodyComponent> bm = Mappers.body;
        private EntityInteractionManager interactionManager = EntityInteractionManager.getInstance();

        @Override
        public int compare(Entity e1, Entity e2) {
            // First check if there's a specific interaction rule for these entities
            if (interactionManager.shouldRenderAbove(e1, e2)) {
                return 1; // e1 should be above e2
            }
            if (interactionManager.shouldRenderAbove(e2, e1)) {
                return -1; // e2 should be above e1
            }

            // If no specific interaction rule, continue with normal priority comparison
            int priorityDiff = getPriorityDifference(e1, e2);

            if (priorityDiff == 0)
                return getVerticalPositionDifferente(e1, e2);

            return priorityDiff;
        }

        private int getVerticalPositionDifferente(Entity e1, Entity e2) {
            PositionComponent p1 = pm.get(e1);
            PositionComponent p2 = pm.get(e2);
            BodyComponent b2 = bm.get(e2);

            if (b2 != null) {
                List<GridPoint2> walkablePoints = b2.generateAbsolutePositions(p2.getGridPosition());
                GridPoint2 position = p1.getGridPosition();
                for (GridPoint2 point : walkablePoints) {
                    if (position.x < point.x - 0.5f && position.x > point.x + 0.5f) {
                        if (position.y < point.y + 1) {
                            return 1;
                        }
                    }
                }
            }
            return (int) ((p2.getY() - p1.getY()) * DIFF_MULTIPLIER);
        }


        private int getPriorityDifference(Entity e1, Entity e2) {
            RenderComponent r1 = rm.get(e1);
            RenderComponent r2 = rm.get(e2);

            return r2.getPriority().getValue() - r1.getPriority().getValue();
        }
    }
}
