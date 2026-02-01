package com.liquidpixel.main.systems.render;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.DoNotRenderComponent;
import com.liquidpixel.main.components.FoundationComponent;
import com.liquidpixel.main.interfaces.IShader;
import com.liquidpixel.main.model.render.OutlineShader;
import com.liquidpixel.sprite.model.GameSprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.components.render.RenderComponent;
import com.liquidpixel.main.components.render.ShaderComponent;
import com.liquidpixel.main.components.items.InStorageComponent;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.utils.Dimensions;
import com.liquidpixel.main.utils.Mappers;

import java.util.*;

public class RenderSystem extends CachedSortedIteratingSystem {
    private ComponentMapper<PositionComponent> pm = Mappers.position;
    private ComponentMapper<RenderComponent> rm = Mappers.render;
    private ComponentMapper<ShaderComponent> sm = Mappers.shader;
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
    private boolean shadersEnabled = false;

    // Reusable objects to avoid GC
    private final Vector2 tempPos = new Vector2();
    private final GridPoint2 tempCell = new GridPoint2();

    public RenderSystem() {
        super(
            Family.all(PositionComponent.class, RenderComponent.class)
                .exclude(InStorageComponent.class, FoundationComponent.class, DoNotRenderComponent.class).get(),
            new ZComparator()
        );
        this.batch = GameResources.get().getBatch();
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
//        analyzeRenderingPerformance(getVisibleEntities());

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

            for (GameSprite sprite : renderComponent.getSprites()) {
                float x = pos.x - Dimensions.toMeters(sprite.getOriginX());
                float y = pos.y - Dimensions.toMeters(sprite.getOriginY());
                float width = Dimensions.toMeters(sprite.getRegionWidth());
                float height = Dimensions.toMeters(sprite.getRegionHeight());

                if (!isInFrustum(x, y, width, height, 0)) {
                    continue;
                }

                // Get the color with proper alpha from render component
                Color renderColor = renderComponent.getColor();

                // Check if entity has a shader component (only if shaders are enabled)
                IShader shader = null;
                if (shadersEnabled && sm.has(entity)) {
                    shader = sm.get(entity).getShader(sprite.getSpriteName());
                    if (shader == null) {
                        shader = sm.get(entity).getShader("default");
                    }
                }

                // Use the texture as the key
                Texture textureKey = sprite.getTexture();

                List<RenderCommand> commands = renderCommands.get(textureKey);
                if (commands == null) {
                    commands = new ArrayList<>();
                    renderCommands.put(textureKey, commands);
                    textureKeys.add(textureKey);
                }

                commands.add(new RenderCommand(
                    sprite, x, y, width, height,
                    renderColor,
                    sprite.getScaleX(), sprite.getScaleY(), sprite.getRotation(),
                    shader
                ));
            }
        }

        // Now render in batches
        ShaderProgram currentShader = null;

        // Debug counters
        int shaderSwitches = 0;
        int actualDrawCalls = 0;
        int textureBindings = 0;

//        System.out.println("=== RENDER EXECUTION ===");
//        System.out.println("Shaders: " + (shadersEnabled ? "ENABLED" : "DISABLED"));
//        System.out.println("Texture keys order: " + textureKeys.size());

        // Now render in batches
        for (Object textureKey : textureKeys) {
            List<RenderCommand> commands = renderCommands.get(textureKey);
            if (commands.isEmpty()) continue;

            textureBindings++;
//            System.out.println("Rendering " + commands.size() + " sprites with texture: " + textureKey);

            // If shaders are disabled, we'll use null shader for everything
            ShaderProgram targetShader = null;
            IShader shaderObject = null;

            if (shadersEnabled) {
                // Get the shader for this batch
                RenderCommand firstCmd = commands.get(0);
                shaderObject = firstCmd.shader;
                targetShader = shaderObject != null ? shaderObject.getShaderProgram() : null;
            }

            // Only switch shader if necessary
            if ((currentShader == null && targetShader != null) ||
                (currentShader != null && targetShader == null) ||
                (currentShader != null && targetShader != null && !currentShader.equals(targetShader))) {

                batch.flush(); // Only flush when shader changes
                batch.setShader(targetShader);
                currentShader = targetShader;

                if (shadersEnabled && shaderObject != null) {
                    // Update texture size if it's an OutlineShader
                    if (shaderObject instanceof OutlineShader) {
                        ((OutlineShader) shaderObject).updateTextureSize(commands.get(0).sprite);
                    }
                    shaderObject.apply(targetShader, batch);
                }
                shaderSwitches++;
//                System.out.println("Shader switch #" + shaderSwitches);
            }

            // Render all commands with this texture
            for (RenderCommand cmd : commands) {
                batch.setColor(cmd.color);
                batch.draw(cmd.sprite, cmd.x, cmd.y,
                    cmd.sprite.getOriginX(), cmd.sprite.getOriginY(),
                    cmd.width, cmd.height,
                    cmd.scaleX, cmd.scaleY, cmd.rotation
                );
                actualDrawCalls++;
            }
        }

        // Reset to default shader
        if (currentShader != null) {
            batch.flush();
            batch.setShader(null);
        }

        batch.setColor(Color.WHITE);

//        System.out.println("Actual draw calls: " + actualDrawCalls);
//        System.out.println("Shader switches: " + shaderSwitches);
//        System.out.println("Texture bindings: " + textureBindings);
//        System.out.println("======================");
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
        final IShader shader;

        RenderCommand(GameSprite sprite, float x, float y, float width, float height,
                      Color color, float scaleX, float scaleY, float rotation, IShader shader) {
            this.sprite = sprite;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
            this.scaleX = scaleX;
            this.scaleY = scaleY;
            this.rotation = rotation;
            this.shader = shader;
        }
    }

    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
    }

    // Add this to your RenderSystem class
    private void analyzeRenderingPerformance(List<Entity> entities) {
        // Count textures being used
        Map<Texture, Integer> textureUsage = new HashMap<>();
        Set<Object> uniqueTextureObjects = new HashSet<>();
        int totalSprites = 0;

        for (Entity entity : entities) {
            RenderComponent renderComponent = rm.get(entity);
            if (renderComponent.getSprites().isEmpty()) continue;

            for (GameSprite sprite : renderComponent.getSprites()) {
                Texture texture = sprite.getTexture();
                uniqueTextureObjects.add(texture);
                textureUsage.put(texture, textureUsage.getOrDefault(texture, 0) + 1);

                // Print class info for each texture
                System.out.println("Sprite texture class: " + texture.getClass().getName() +
                    ", hash: " + System.identityHashCode(texture));

                totalSprites++;
            }
        }

        System.out.println("=== RENDER ANALYSIS ===");
        System.out.println("Total sprites to render: " + totalSprites);
        System.out.println("Unique texture objects: " + uniqueTextureObjects.size());
        System.out.println("Unique textures by file: " + textureUsage.size());

        // Check if HashMap is working properly with these textures
        Map<Texture, String> testMap = new HashMap<>();
        for (Texture texture : textureUsage.keySet()) {
            testMap.put(texture, texture.toString());
        }
        System.out.println("Test map size: " + testMap.size());

        System.out.println("======================");
    }
}
