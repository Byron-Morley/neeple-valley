package com.liquidpixel.main.systems.ai;


import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.pathfinding.components.TraverseComponent;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.utils.PhasedIteratingSystem;

public class DebugTraverseSystem extends PhasedIteratingSystem {
    private static final Color FADED_BLUE = new Color(Color.BLUE.r, Color.BLUE.g, Color.BLUE.b, 0.4f);
    private static final Color FADED_RED = new Color(Color.RED.r, Color.RED.g, Color.RED.b, 0.4f);
    Camera camera;
    ShapeRenderer renderer;
    SpriteBatch spriteBatch;
    public boolean DEBUG = true;

    public DebugTraverseSystem() {
        super(Family.all(TraverseComponent.class).get());
        this.camera = GameResources.get().getCamera();
        this.spriteBatch = GameResources.get().getBatch();
        this.renderer = GameResources.get().getShapeRenderer();
    }

    @Override
    protected void beforeFrame() {
        spriteBatch.end();
        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (DEBUG) {
            renderer.setProjectionMatrix(camera.combined);
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(new Color(1f, 1f, 1f, 0.4f));
//            TraverseComponent traverseComponent = entity.getComponent(TraverseComponent.class);
            //            traverseComponent.getWaypoints().forEach(this::drawNode);
            renderer.setColor(FADED_RED);
            renderer.end();
        }
    }

    private void drawNode(Vector2 node) {
        renderer.rect(node.x, node.y, 1f, 1f);
    }

    @Override
    protected void afterFrame() {
        Gdx.gl.glDisable(GL30.GL_BLEND);
        spriteBatch.begin();
    }
}
