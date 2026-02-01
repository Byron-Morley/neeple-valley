package com.liquidpixel.main.systems.render;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.ai.pfa.Connection;
import com.liquidpixel.main.ai.pathfinding.FlatTiledNode;
import com.liquidpixel.main.ai.pathfinding.MapGraph;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.model.sprite.NodeType;

import java.util.Map;

public class DebugRenderSystem extends EntitySystem {

    ShapeRenderer renderer;
    SpriteBatch spriteBatch;
    OrthographicCamera camera;
    MapGraph mapGraph;
    public static boolean RENDER_DEBUG = true;
    public static boolean RENDER_CONNECTIONS = false; // Flag to toggle connection rendering
    public static boolean RENDER_COST_CONNECTIONS = false; // Flag to toggle cost-based connection rendering
    float MAX_COST = 2f; // Maximum connection cost for color scaling
    float CONNECTION_LINE_WIDTH = 3f; // Thickness of connection lines

    private final Color tempColor = new Color(); // Reusable color object to avoid GC

    public DebugRenderSystem(MapGraph mapGraph) {
        this.renderer = GameResources.get().getShapeRenderer();
        this.spriteBatch = GameResources.get().getBatch();
        this.camera = GameResources.get().getCamera();
        this.mapGraph = mapGraph;
    }

    protected void beforeFrame() {
        spriteBatch.end();
        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
    }

    public void update(float deltaTime) {
        if (RENDER_DEBUG) {
            beforeFrame();
            renderer.setProjectionMatrix(camera.combined);

            // Render node squares
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(1, 0, 0, 0.2f);

            for (Map.Entry<GridPoint2, FlatTiledNode> entry : mapGraph.getNodes().entrySet()) {
                GridPoint2 position = entry.getKey();
                FlatTiledNode node = entry.getValue();

                // occupied but walkable
                if (!node.getEntities().isEmpty()) {
                    renderer.setColor(0.2f, 1, 0, 0.2f);
                    renderer.rect(position.x, position.y, 1, 1);
                }

                // not walkable
                if (node.getType() != NodeType.TILE_FLOOR && camera.frustum.pointInFrustum(position.x, position.y, 0)) {
                    renderer.setColor(.7f, 0f, 0, 0.5f);
                    renderer.rect(position.x, position.y, 1, 1);
                }
            }
            renderer.end();

            // Render connections between nodes
            if (RENDER_CONNECTIONS) {
                if (RENDER_COST_CONNECTIONS) {
                    renderCostBasedConnections();
                } else {
                    renderer.begin(ShapeRenderer.ShapeType.Line);
                    renderer.setColor(1, 1, 1, 0.3f); // Thin white lines with transparency

                    for (Map.Entry<GridPoint2, FlatTiledNode> entry : mapGraph.getNodes().entrySet()) {
                        FlatTiledNode node = entry.getValue();
                        GridPoint2 fromPos = entry.getKey();

                        // Get all connections from this node
                        Array<Connection<FlatTiledNode>> connections = mapGraph.getConnections(node);
                        if (connections != null) {
                            for (Connection<FlatTiledNode> connection : connections) {
                                FlatTiledNode toNode = connection.getToNode();
                                // Draw line from center of one node to center of the other
                                renderer.line(
                                    fromPos.x + 0.5f, fromPos.y + 0.5f,
                                    toNode.getX() + 0.5f, toNode.getY() + 0.5f
                                );
                            }
                        }
                    }
                    renderer.end();
                }
            }

            afterFrame();
        }
    }

    /**
     * Renders connections with colors based on their cost
     * Blue (0, 0, 1) for lowest cost to Green (0, 1, 0) for highest cost
     */
    private void renderCostBasedConnections() {
        // Set line width before beginning
        Gdx.gl.glLineWidth(CONNECTION_LINE_WIDTH);

        renderer.begin(ShapeRenderer.ShapeType.Line);

        for (Map.Entry<GridPoint2, FlatTiledNode> entry : mapGraph.getNodes().entrySet()) {
            FlatTiledNode node = entry.getValue();
            GridPoint2 fromPos = entry.getKey();

            // Get all connections from this node
            Array<Connection<FlatTiledNode>> connections = mapGraph.getConnections(node);
            if (connections != null) {
                for (Connection<FlatTiledNode> connection : connections) {
                    FlatTiledNode toNode = connection.getToNode();

                    // Get connection cost and normalize it between 0 and 1
                    float cost = connection.getCost();
                    float normalizedCost = MathUtils.clamp(cost / MAX_COST, 0f, 1f);

                    // Interpolate between blue (0,0,1) and green (0,1,0) based on cost
                    tempColor.set(0f, normalizedCost, 1f - normalizedCost, 0.5f);
                    renderer.setColor(tempColor);

                    // Draw line from center of one node to center of the other
                    renderer.line(
                        fromPos.x + 0.5f, fromPos.y + 0.5f,
                        toNode.getX() + 0.5f, toNode.getY() + 0.5f
                    );
                }
            }
        }
        renderer.end();

        // Reset line width to default after drawing
        Gdx.gl.glLineWidth(1f);
    }

    protected void afterFrame() {
        Gdx.gl.glDisable(GL30.GL_BLEND);
        spriteBatch.begin();
    }

    // Method to toggle connection rendering
    public void setRenderConnections(boolean renderConnections) {
        this.RENDER_CONNECTIONS = renderConnections;
    }

    // Method to toggle cost-based connection rendering
    public void setRenderCostConnections(boolean renderCostConnections) {
        this.RENDER_COST_CONNECTIONS = renderCostConnections;
    }

    // Method to set the maximum cost value for color scaling
    public void setMaxCost(float maxCost) {
        this.MAX_COST = maxCost;
    }

    // Method to set the thickness of connection lines
    public void setConnectionLineWidth(float width) {
        this.CONNECTION_LINE_WIDTH = width;
    }
}
