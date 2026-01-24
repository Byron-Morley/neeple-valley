package com.liquidpixel.main.systems.render;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.ShapeComponent;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.main.utils.PhasedIteratingSystem;
import com.liquidpixel.main.utils.shape.Shape;

public class ShapeRenderSystem extends PhasedIteratingSystem {

    ShapeRenderer renderer;
    SpriteBatch spriteBatch;
    OrthographicCamera camera;

    public ShapeRenderSystem() {
        super(Family.all(ShapeComponent.class, PositionComponent.class).get());
        this.renderer = GameResources.get().getShapeRenderer();
        this.spriteBatch = GameResources.get().getBatch();
        this.camera = GameResources.get().getCamera();
    }

    @Override
    protected void beforeFrame() {
        spriteBatch.end();
        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ShapeComponent shapeComponent = Mappers.shape.get(entity);
        PositionComponent positionComponent = Mappers.position.get(entity);
        Vector2 position = positionComponent.getPosition();
        Shape shape = shapeComponent.getShape();

        renderer.setProjectionMatrix(camera.combined);

        Gdx.gl20.glLineWidth(shapeComponent.getLineThickness());

        renderer.begin(shape.getShapeType());
        renderer.setColor(shape.getColor());

        Vector2 pos = shapeComponent.getRenderPositionStrategy().process(position.x, position.y);

        if (camera.frustum.pointInFrustum(pos.x, pos.y, 0)) {
            shape.render(renderer, pos);
        }

        renderer.end();
    }

    @Override
    protected void afterFrame() {
        Gdx.gl.glDisable(GL30.GL_BLEND);
        spriteBatch.begin();
    }
}
