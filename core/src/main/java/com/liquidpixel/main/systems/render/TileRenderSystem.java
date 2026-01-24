package com.liquidpixel.main.systems.render;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.sprite.TileableComponent;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.utils.Mappers;


public class TileRenderSystem extends IteratingSystem {
    private ComponentMapper<TileableComponent> tm = Mappers.tileset;
    private ComponentMapper<PositionComponent> pm = Mappers.position;
    IItemService itemService;

    public TileRenderSystem(IItemService itemService) {
        super(Family.all(TileableComponent.class, PositionComponent.class).get());
        this.itemService = itemService;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        checkTilePositionChange(entity);
    }

    private void checkTilePositionChange(Entity entity) {

        TileableComponent tileableComponent = tm.get(entity);
        PositionComponent positionComponent = pm.get(entity);
        GridPoint2 position = positionComponent.getGridPosition();
        GridPoint2 lastKnownPosition = tileableComponent.getLastKnownPosition();

        if (!position.equals(lastKnownPosition)) {
            if (lastKnownPosition != null) itemService.setupTileset(entity, lastKnownPosition);
            tileableComponent.setLastKnownPosition(position);
            itemService.setupTileset(entity, position);
        }
    }
}
