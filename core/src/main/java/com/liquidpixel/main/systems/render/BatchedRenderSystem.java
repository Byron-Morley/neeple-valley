package com.liquidpixel.main.systems.render;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.DoNotRenderComponent;
import com.liquidpixel.main.helpers.ItemHelper;
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
    private Array<String> missingSprites = new Array<>();

    private float timeSinceLastSort = 0f;
    private static final float SORT_INTERVAL = 0.05f; // 20x per second (tune)


    public BatchedRenderSystem() {
        super(Family.all(PositionComponent.class, RenderComponent.class).exclude(DoNotRenderComponent.class).get(), new ZComparator());
        this.batch = GameResources.get().getBatch();
    }

    @Override
    public void update(float deltaTime) {
        timeSinceLastSort += deltaTime;
        if (timeSinceLastSort >= SORT_INTERVAL) {
            forceSort();
            timeSinceLastSort = 0f;
        }
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent positionComponent = pm.get(entity);
        RenderComponent renderComponent = rm.get(entity);
        List<GameSprite> sprites = renderComponent.getSprites();
        OrthographicCamera camera = GameResources.get().getCamera();

        if (!spriteExists(entity, sprites)) return;

        for (GameSprite sprite : sprites) {
            Vector2 pos = renderComponent.getRenderPositionStrategy().process(
                positionComponent.getX(), positionComponent.getY());

            float originX = sprite.getOriginX();
            float originY = sprite.getOriginY();

            float x = pos.x - originX;
            float y = pos.y - originY;
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

            if (!inFrustum) continue;

            batch.setColor(renderComponent.getColor());
            batch.draw(sprite, x, y,
                sprite.getOriginX(), sprite.getOriginY(),
                width, height,
                sprite.getScaleX(), sprite.getScaleY(),
                sprite.getRotation()
            );
        }
    }

    private boolean spriteExists(Entity entity, List<GameSprite> sprites) {
        String name = ItemHelper.getName(entity);
        if (sprites.isEmpty()) {
            if (!missingSprites.contains(name, true)) {
                Gdx.app.debug("BatchedRenderSystem", ItemHelper.getName(entity) + ": No sprites found for entity!");
                missingSprites.add(name);
            }
            return false;
        } else {
            if (missingSprites.contains(name, true)) {
                missingSprites.removeValue(name, true);
            }
        }
        return true;
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
                return getVerticalPositionDifference(e1, e2);

            return priorityDiff;
        }

        private int getVerticalPositionDifference(Entity e1, Entity e2) {
            PositionComponent p1 = pm.get(e1);
            PositionComponent p2 = pm.get(e2);
            BodyComponent b2 = bm.get(e2);

            if (b2 != null) {
                List<GridPoint2> walkablePoints = b2.generateAbsolutePositions(p2.getGridPosition());
                GridPoint2 position = p1.getGridPosition();
                for (GridPoint2 point : walkablePoints) {
                    // position.x must be within the point.x range (this was impossible before)
                    if (position.x > point.x - 0.5f && position.x < point.x + 0.5f) {
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
