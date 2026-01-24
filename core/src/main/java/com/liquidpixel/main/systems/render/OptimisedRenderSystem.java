package com.liquidpixel.main.systems.render;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.FoundationComponent;
import com.liquidpixel.main.components.render.RenderComponent;
import com.liquidpixel.sprite.model.GameSprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.components.items.InStorageComponent;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.utils.Dimensions;
import com.liquidpixel.main.utils.Mappers;

import java.util.*;

/**
 * A custom SortedIteratingSystem that handles entity rendering with optimized performance.
 */
public class OptimisedRenderSystem extends CachedSortedIteratingSystem {
    private ComponentMapper<PositionComponent> pm = Mappers.position;
    private ComponentMapper<RenderComponent> rm = Mappers.render;
    private SpriteBatch batch;
    private float timeSinceLastSort = 0;
    private static final float SORT_INTERVAL = 0.1f; // Sort 10 times per second

    // Spatial partitioning grid
    private Map<GridPoint2, List<Entity>> spatialGrid = new HashMap<>();
    private static final int GRID_CELL_SIZE = 5; // In world units

    // Frustum culling optimization
    private OrthographicCamera camera;
    private float camX, camY, halfWidth, halfHeight;

    // Texture sorting optimization
    private final Map<Object, List<RenderCommand>> renderCommands = new HashMap<>();
    private final List<Object> textureKeys = new ArrayList<>();

    private final ZComparator zComparator;

    // Reusable objects to avoid GC
    private final Vector2 tempPos = new Vector2();
    private final GridPoint2 tempCell = new GridPoint2();

    public OptimisedRenderSystem() {
        super(
            Family.all(PositionComponent.class, RenderComponent.class)
                .exclude(InStorageComponent.class, FoundationComponent.class).get(),
            new ZComparator()
        );
        this.batch = GameResources.get().getBatch();
//        this.batch = new SpriteBatch(2000);
        this.zComparator = new ZComparator();
    }

    private void updateSpatialGrid() {
        spatialGrid.clear();

        for (Entity entity : getEntities()) {
            PositionComponent pos = pm.get(entity);
            tempCell.set(
                (int) (pos.getX() / GRID_CELL_SIZE),
                (int) (pos.getY() / GRID_CELL_SIZE)
            );

            List<Entity> cellEntities = spatialGrid.get(tempCell);
            if (cellEntities == null) {
                cellEntities = new ArrayList<>();
                spatialGrid.put(new GridPoint2(tempCell), cellEntities);
            }
            cellEntities.add(entity);
        }
    }

    private void printTextureStats() {
        System.out.println("=== Detailed Texture Usage Statistics ===");
        System.out.println("Total unique textures: " + textureKeys.size());

        for (Object textureKey : textureKeys) {
            List<RenderCommand> commands = renderCommands.get(textureKey);
            System.out.println(textureKey + ": " + commands.size() + " sprites");

            // Print the first few sprite names to help identify patterns
            System.out.println("  Sample sprites:");
            for (int i = 0; i < Math.min(5, commands.size()); i++) {
                System.out.println("    " + commands.get(i).sprite.getSpriteName());
            }
        }
        System.out.println("==============================");
    }

    @Override
    public void update(float deltaTime) {
        // Update camera info for frustum culling
        camera = GameResources.get().getCamera();
        camX = camera.position.x;
        camY = camera.position.y;
        halfWidth = camera.viewportWidth * camera.zoom / 2;
        halfHeight = camera.viewportHeight * camera.zoom / 2;

        // Set the projection matrix of our batch to match the camera
        batch.setProjectionMatrix(camera.combined);

        // Sort less frequently
        timeSinceLastSort += deltaTime;
        if (timeSinceLastSort >= SORT_INTERVAL) {
            forceSort();
            timeSinceLastSort = 0;
            clearComparatorCache();
            updateSpatialGrid();

            // Print texture stats occasionally
            if (timeSinceLastSort % 5 < SORT_INTERVAL) {
//                printTextureStats();
            }
        }

        // Get visible entities
        List<Entity> visibleEntities = getVisibleEntities();

        // Sort visible entities
        Collections.sort(visibleEntities, zComparator);

        // Batch render visible entities
        batchRenderSprites(visibleEntities);
    }


    private List<Entity> getVisibleEntities() {
        float width = camera.viewportWidth * camera.zoom;
        float height = camera.viewportHeight * camera.zoom;

        int minCellX = (int) ((camX - width / 2) / GRID_CELL_SIZE) - 1;
        int maxCellX = (int) ((camX + width / 2) / GRID_CELL_SIZE) + 1;
        int minCellY = (int) ((camY - height / 2) / GRID_CELL_SIZE) - 1;
        int maxCellY = (int) ((camY + height / 2) / GRID_CELL_SIZE) + 1;

        // Collect visible entities
        List<Entity> visibleEntities = new ArrayList<>(100); // Pre-allocate capacity
        for (int x = minCellX; x <= maxCellX; x++) {
            for (int y = minCellY; y <= maxCellY; y++) {
                tempCell.set(x, y);
                List<Entity> entitiesInCell = spatialGrid.get(tempCell);
                if (entitiesInCell != null) {
                    visibleEntities.addAll(entitiesInCell);
                }
            }
        }

        return visibleEntities;
    }

    // Modify batchRenderSprites method
    private void batchRenderSprites(List<Entity> entities) {
        // Clear previous render commands
        renderCommands.clear();
        textureKeys.clear();

        // Group sprites by texture
        for (Entity entity : entities) {
            RenderComponent renderComponent = rm.get(entity);
            PositionComponent positionComponent = pm.get(entity);

            if (renderComponent.getSprites().isEmpty()) continue;

            tempPos.set(positionComponent.getX(), positionComponent.getY());
            Vector2 pos = renderComponent.getRenderPositionStrategy().process(
                tempPos.x, tempPos.y);

            // In batchRenderSprites method
            for (GameSprite sprite : renderComponent.getSprites()) {
                float x = pos.x - Dimensions.toMeters(sprite.getOriginX());
                float y = pos.y - Dimensions.toMeters(sprite.getOriginY());
                float width = Dimensions.toMeters(sprite.getRegionWidth());
                float height = Dimensions.toMeters(sprite.getRegionHeight());

                // Use the isInFrustum method for culling
                if (!isInFrustum(x, y, width, height, 0)) {
                    continue;
                }

                // Add to render commands
                Object textureKey = sprite.getTexture();
                List<RenderCommand> commands = renderCommands.get(textureKey);
                if (commands == null) {
                    commands = new ArrayList<>();
                    renderCommands.put(textureKey, commands);
                    textureKeys.add(textureKey);
                }

                commands.add(new RenderCommand(
                    sprite, x, y, width, height,
                    renderComponent.getColor(),
                    sprite.getScaleX(), sprite.getScaleY(), sprite.getRotation()
                ));
            }

        }

        // Render all sprites grouped by texture
//        batch.begin();
        // Set optimal blend function
//        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        for (Object textureKey : textureKeys) {
            List<RenderCommand> commands = renderCommands.get(textureKey);

            // Flush only when switching textures
            if (textureKey != textureKeys.get(0)) {
                batch.flush();
            }

            for (RenderCommand cmd : commands) {
                batch.setColor(cmd.color);
                batch.draw(cmd.sprite, cmd.x, cmd.y,
                    cmd.sprite.getOriginX(), cmd.sprite.getOriginY(),
                    cmd.width, cmd.height,
                    cmd.scaleX, cmd.scaleY, cmd.rotation
                );
            }
        }
//        batch.end();
    }

    // Fast frustum culling using AABB
    private boolean isInFrustum(float x, float y, float width, float height, float margin) {
        return x + width + margin > camX - halfWidth &&
            x - margin < camX + halfWidth &&
            y + height + margin > camY - halfHeight &&
            y - margin < camY + halfHeight;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // This method is no longer used as we're using batchRenderSprites instead
    }

    // Optimized render command class (more memory efficient than RenderData)
    private static class RenderCommand {
        final GameSprite sprite;
        final float x, y, width, height, scaleX, scaleY, rotation;
        final Color color;

        RenderCommand(GameSprite sprite, float x, float y, float width, float height,
                      Color color, float scaleX, float scaleY, float rotation) {
            this.sprite = sprite;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
            this.scaleX = scaleX;
            this.scaleY = scaleY;
            this.rotation = rotation;
        }
    }

    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
    }
}
