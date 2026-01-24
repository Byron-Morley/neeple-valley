package com.liquidpixel.main.systems.settlement;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.interfaces.services.ISelectionService;

public class SettlementResourceVisualSystem extends EntitySystem {

    ISelectionService selectionService;
    ShapeRenderer renderer;
    SpriteBatch spriteBatch;
    OrthographicCamera camera;

    public SettlementResourceVisualSystem(ISelectionService selectionService) {
        this.selectionService = selectionService;
        this.renderer = GameResources.get().getShapeRenderer();
        this.spriteBatch = GameResources.get().getBatch();
        this.camera = GameResources.get().getCamera();
    }

    protected void beforeFrame() {
        spriteBatch.end();
        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void update(float deltaTime) {

//        if (selectionService.getMode().equals(SelectionManager.Mode.CONNECT)) {
//
//            Entity settlement = selectionService.getSelectedSettlement();
//            if (settlement == null) return;
//
//            List<Entity> resources = Mappers.settlement.get(settlement).getResources();
//            PositionComponent settlementPos = Mappers.position.get(settlement);
//            RenderComponent settlementSize = Mappers.render.get(settlement);
//
//            beforeFrame();
//            renderer.setProjectionMatrix(camera.combined);
//            renderer.begin(ShapeRenderer.ShapeType.Line);
//            renderer.setColor(1, 1, 1, 1);
//            Gdx.gl.glLineWidth(5f);
//
//            for (Entity resourceEntity : resources) {
//                PositionComponent resourcePos = Mappers.position.get(resourceEntity);
//                RenderComponent resourceSize = Mappers.render.get(resourceEntity);
//
//                // Calculate center points
//                float startX = settlementPos.getX() + (float) settlementSize.getWidth() / 2;
//                float startY = settlementPos.getY() + (float) settlementSize.getHeight() / 2;
//                float endX = resourcePos.getX() + (float) resourceSize.getWidth() / 2;
//                float endY = resourcePos.getY() + (float) resourceSize.getHeight() / 2;
//
//                float distance = Vector2.dst(startX, startY, endX, endY);
//                float midY = (startY + endY) / 2;
//                float curveHeight = distance * 0.5f;
//
//                float controlX1 = startX + (endX - startX) / 4;
//                float controlY1 = midY + curveHeight;
//                float controlX2 = startX + (endX - startX) * 3 / 4;
//                float controlY2 = midY + curveHeight;
//
//                renderer.curve(
//                    startX, startY,
//                    controlX1, controlY1,
//                    controlX2, controlY2,
//                    endX, endY,
//                    32
//                );
//            }
//            renderer.end();
//            Gdx.gl.glLineWidth(1f);
//
//            afterFrame();
//        }
    }

    protected void afterFrame() {
        Gdx.gl.glDisable(GL30.GL_BLEND);
        spriteBatch.begin();
    }
}
