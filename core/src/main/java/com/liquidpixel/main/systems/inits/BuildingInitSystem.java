package com.liquidpixel.main.systems.inits;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.BodyComponent;
import com.liquidpixel.main.components.BuildingInitComponent;
import com.liquidpixel.main.components.colony.BuildingComponent;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.utils.Mappers;

import java.util.List;

public class BuildingInitSystem extends IteratingSystem {
    IWorldMap worldMap;

    public BuildingInitSystem(IWorldMap worldMap) {
        super(Family.all(BuildingInitComponent.class, BuildingComponent.class, PositionComponent.class, BodyComponent.class).get());
        this.worldMap = worldMap;
    }

    @Override
    protected void processEntity(Entity building, float deltaTime) {

        BuildingComponent buildingComponent = Mappers.building.get(building);

        if (buildingComponent.isSpawnDirtPatch() && !buildingComponent.isHasDirtPatch()) {
            BodyComponent bodyComponent = Mappers.body.get(building);
            PositionComponent positionComponent = Mappers.position.get(building);


            List<GridPoint2> positions = bodyComponent.generateAbsolutePositions(new GridPoint2(1, 1));
            int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
            int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

            for (GridPoint2 pos : positions) {
                minX = Math.min(minX, pos.x);
                maxX = Math.max(maxX, pos.x);
                minY = Math.min(minY, pos.y);
                maxY = Math.max(maxY, pos.y);
            }

            int width = maxX - minX + 1;
            int height = maxY - minY;

            worldMap.createDirtPatch(new GridPoint2(positionComponent.getGridPosition()).add(new GridPoint2(bodyComponent.offset)), width + 1, height + 1);
            buildingComponent.setHasDirtPatch(true);


            reAddObstacles(positionComponent, bodyComponent);
        }

        building.remove(BuildingInitComponent.class);
    }

    private void reAddObstacles(PositionComponent positionComponent, BodyComponent bodyComponent) {
        GridPoint2 position = positionComponent.getGridPosition();
        List<GridPoint2> positions = bodyComponent.generateAbsolutePositions(position);
        for (GridPoint2 point : positions) {
            worldMap.addObstacle(point, 1, 1);
        }
    }
}
