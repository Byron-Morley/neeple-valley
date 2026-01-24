package com.liquidpixel.main.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.BodyComponent;
import com.liquidpixel.item.components.SpectralPickupComponent;
import com.liquidpixel.main.components.camera.CameraComponent;
import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.listeners.build.ObstacleBuildListener;
import com.liquidpixel.main.utils.Mappers;

import java.util.List;

public class MapUpdateSystem extends IteratingSystem {

    IMapService mapService;
    IWorldMap worldMap;
    ObstacleBuildListener obstacleBuildListener;

    public MapUpdateSystem(IMapService mapService, ObstacleBuildListener obstacleBuildListener) {
        super(Family.all(PositionComponent.class).exclude(CameraComponent.class, SpectralPickupComponent.class).get());
        this.mapService = mapService;
        this.worldMap = mapService.getWorldMap();
        this.obstacleBuildListener = obstacleBuildListener;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent positionComponent = Mappers.position.get(entity);
        GridPoint2 position = positionComponent.getGridPosition();
        GridPoint2 prevPosition = positionComponent.getPreviousPosition();

        if (prevPosition == null) {
            positionComponent.setPreviousPosition(new GridPoint2(position));

        } else if (!position.equals(prevPosition)) {
            worldMap.removeEntity(entity, prevPosition);

            if (Mappers.body.has(entity)) {
                obstacleBuildListener.removeBodyPositions(entity);
                obstacleBuildListener.addBodyPositions(entity);
            }

            positionComponent.setPreviousPosition(new GridPoint2(position));
            worldMap.addEntity(entity, position);
        }
    }

        private void removePositions(Entity entity, GridPoint2 position) {
        BodyComponent bodyComponent = Mappers.body.get(entity);
        List<GridPoint2> positions = bodyComponent.generateAbsolutePositions(position);

        for (GridPoint2 point : positions) {
            worldMap.removeObstacle(point);
            worldMap.removeEntity(entity, point);
        }
    }

    private void addPositions(Entity entity, GridPoint2 position) {
        BodyComponent bodyComponent = Mappers.body.get(entity);
        List<GridPoint2> positions = bodyComponent.generateAbsolutePositions(position);

        for (GridPoint2 point : positions) {
            worldMap.addObstacle(point, 1, 1);
            worldMap.addEntity(entity, point);
        }
    }
}
