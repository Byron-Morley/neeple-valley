package com.liquidpixel.main.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.ai.pathfinding.FlatTiledNode;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.generators.map.WorldMap;
import com.liquidpixel.main.interfaces.managers.IChunkManager;
import com.liquidpixel.main.interfaces.Renderable;
import com.liquidpixel.main.model.terrain.Chunk;
import com.liquidpixel.main.utils.LoopUtils;

import com.badlogic.gdx.utils.Array;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;

import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.graphics.g2d.Batch.*;
import static com.badlogic.gdx.graphics.g2d.Batch.V4;
import static com.liquidpixel.main.utils.Dimensions.PX_PER_METER;

public class ChunkRenderer implements Renderable, IChunkManager {

    Map<GridPoint2, Chunk> chunks;
    Chunk activeChunk;
    int CULLING_RADIUS = 8;
    SpriteBatch batch;
    WorldMap worldMap;
    TextureRegion gridTexture;
    TextureRegion gridOccupiedTexture;


    public ChunkRenderer(WorldMap worldMap, ISpriteFactory spriteFactory) {
        this.worldMap = worldMap;
        chunks = new HashMap<>();
        batch = GameResources.get().getBatch();
        gridTexture = spriteFactory.getTextureWithFallback("grid", -1);
        gridOccupiedTexture = spriteFactory.getTextureWithFallback("red-grid", -1);
    }

    @Override
    public void render(float delta) {
        if (activeChunk != null) {

            int chunkX = activeChunk.getLocation().x;
            int chunkY = activeChunk.getLocation().y;

            LoopUtils.insideOut(
                worldMap.getMapConfiguration().getChunkCountX(),
                worldMap.getMapConfiguration().getChunkCountY(),
                CULLING_RADIUS,
                chunkX,
                chunkY,
                (coordinate) -> {
                    Chunk chunk = chunks.get(coordinate);
                    if (chunk != null) renderChunk(chunk);
                }
            );
        }
        checkActiveChunkPosition();
    }

    public void renderChunk(Chunk chunk) {
        // Let's go back to your original implementation but with batching
        final Color batchColor = batch.getColor();
        final float color = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, batchColor.a * chunk.getOpacity());
        final float layerTileWidth = chunk.getTileWidth() / PX_PER_METER;
        final float layerTileHeight = chunk.getTileHeight() / PX_PER_METER;
        final int col1 = chunk.getPixelLocation().y;
        final int col2 = chunk.getHeight() + chunk.getPixelLocation().y;
        final int row1 = chunk.getPixelLocation().x;
        final int row2 = chunk.getWidth() + chunk.getPixelLocation().x;

        // Group tiles by texture
        Map<Texture, Array<float[]>> tileVerticesByTexture = new HashMap<>();

        float y = row2 * layerTileHeight;
        float xStart = col1 * layerTileWidth;
        int dx = chunk.getWidth();

        // Use the original logic for calculating positions and UVs
        for (int row = row2; row >= row1; row--) {
            int dy = 0;
            float x = xStart;
            for (int col = col1; col < col2; col++) {
                final TiledMapTileLayer.Cell cell = chunk.getCell(dx, dy);
                if (cell == null) {
                    x += layerTileWidth;
                    continue;
                }
                final TiledMapTile tile = cell.getTile();
                if (tile != null) {
                    TextureRegion region = tile.getTextureRegion();

                    // Calculate positions exactly as in your original code
                    float y1 = x + tile.getOffsetX() / PX_PER_METER;
                    float x1 = y + tile.getOffsetY() / PX_PER_METER;
                    float x2 = x1 + region.getRegionWidth() / PX_PER_METER;
                    float y2 = y1 + region.getRegionHeight() / PX_PER_METER;

                    // Get UVs with cell transformations applied
                    float u1 = region.getU();
                    float v1 = region.getV2();
                    float u2 = region.getU2();
                    float v2 = region.getV();

                    // Apply cell transformations
                    if (cell.getFlipHorizontally()) {
                        float temp = u1;
                        u1 = u2;
                        u2 = temp;
                    }

                    if (cell.getFlipVertically()) {
                        float temp = v1;
                        v1 = v2;
                        v2 = temp;
                    }

                    // Handle rotation
                    int rotation = cell.getRotation();
                    if (rotation != 0) {
                        float tempU, tempV;
                        switch (rotation) {
                            case TiledMapTileLayer.Cell.ROTATE_90:
                                tempU = u1;
                                u1 = u2;
                                u2 = tempU;
                                tempV = v1;
                                v1 = v2;
                                v2 = tempV;
                                break;
                            case TiledMapTileLayer.Cell.ROTATE_180:
                                float temp = u1;
                                u1 = u2;
                                u2 = temp;
                                temp = v1;
                                v1 = v2;
                                v2 = temp;
                                break;
                            case TiledMapTileLayer.Cell.ROTATE_270:
                                tempU = u1;
                                u1 = u2;
                                u2 = tempU;
                                tempV = v1;
                                v1 = v2;
                                v2 = tempV;
                                break;
                        }
                    }

                    // Create vertex array for this tile
                    float[] vertices = new float[20];
                    vertices[X1] = x1;
                    vertices[Y1] = y1;
                    vertices[C1] = color;
                    vertices[U1] = u1;
                    vertices[V1] = v1;

                    vertices[X2] = x1;
                    vertices[Y2] = y2;
                    vertices[C2] = color;
                    vertices[U2] = u1;
                    vertices[V2] = v2;

                    vertices[X3] = x2;
                    vertices[Y3] = y2;
                    vertices[C3] = color;
                    vertices[U3] = u2;
                    vertices[V3] = v2;

                    vertices[X4] = x2;
                    vertices[Y4] = y1;
                    vertices[C4] = color;
                    vertices[U4] = u2;
                    vertices[V4] = v1;

                    // Add to the appropriate texture group
                    com.badlogic.gdx.graphics.Texture texture = region.getTexture();
                    if (!tileVerticesByTexture.containsKey(texture)) {
                        tileVerticesByTexture.put(texture, new Array<>());
                    }
                    tileVerticesByTexture.get(texture).add(vertices);
                }
                dy++;
                x += layerTileWidth;
            }
            dx--;
            y -= layerTileHeight;
        }

        // Now draw all tiles grouped by texture
        for (Map.Entry<com.badlogic.gdx.graphics.Texture, Array<float[]>> entry : tileVerticesByTexture.entrySet()) {
            com.badlogic.gdx.graphics.Texture texture = entry.getKey();
            Array<float[]> tileVertices = entry.getValue();

            // For each texture, we'll combine all vertices into one large array
            int totalVertices = tileVertices.size * 20; // 20 values per tile
            float[] combinedVertices = new float[totalVertices];

            // Copy all tile vertices into the combined array
            int offset = 0;
            for (float[] vertices : tileVertices) {
                System.arraycopy(vertices, 0, combinedVertices, offset, 20);
                offset += 20;
            }

            // Draw all tiles with this texture in one batch
            batch.draw(texture, combinedVertices, 0, totalVertices);
        }
    }


    @Override
    public void dispose() {
    }

    @Override
    public void addChunk(GridPoint2 location, Chunk chunk) {
        chunks.put(location, chunk);
    }

    @Override
    public void addNode(FlatTiledNode node) {
        worldMap.addNode(node);
    }

    public void setActiveChunk(Chunk activeChunk) {
        this.activeChunk = activeChunk;
    }

    public void checkActiveChunkPosition() {

        OrthographicCamera camera = GameResources.get().getCamera();

        int x = (int) Math.floor(camera.position.x / worldMap.getMapConfiguration().getChunkWidth());
        int y = (int) Math.floor(camera.position.y / worldMap.getMapConfiguration().getChunkHeight());

        GridPoint2 newCoordinates = new GridPoint2(x, y);

        if (activeChunk == null || activeChunk.getLocation().dst(newCoordinates) > 0) {
            Chunk chunk = chunks.get(newCoordinates);

            if (chunk != null) {
                activeChunk = chunk;
            }
        }
    }

    public Map<GridPoint2, Chunk> getChunks() {
        return chunks;
    }

    public void reset() {
        chunks.clear();
        activeChunk = null;
    }
}
