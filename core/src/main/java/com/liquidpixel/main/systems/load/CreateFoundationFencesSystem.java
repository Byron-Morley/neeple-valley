package com.liquidpixel.main.systems.load;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.BodyComponent;
import com.liquidpixel.main.components.FoundationComponent;
import com.liquidpixel.main.components.load.CreateFoundationFencesComponent;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.utils.Mappers;

public class CreateFoundationFencesSystem extends IteratingSystem {

    IItemService itemService;
    IWorldMap worldMap;

    public CreateFoundationFencesSystem(IItemService itemService, IWorldMap worldMap) {
        super(Family.all(CreateFoundationFencesComponent.class).get());
        this.itemService = itemService;
        this.worldMap = worldMap;
    }

    @Override
    protected void processEntity(Entity entity, float v) {

        if (Mappers.foundation.has(entity) && Mappers.body.has(entity)) {

            System.out.println("adding foundation fences");

            PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
            GridPoint2 position = positionComponent.getGridPosition();
            FoundationComponent foundationComponent = Mappers.foundation.get(entity);
            BodyComponent bodyComponent = Mappers.body.get(entity);

            int width = bodyComponent.width;
            int height = bodyComponent.height;
            GridPoint2 pos = new GridPoint2(position);

            worldMap.createDirtPatch(
                pos,
                width + 1,
                height + 1
            );

            int minX = pos.x;
            int maxX = pos.x + width - 1;
            int minY = pos.y;
            int maxY = pos.y + height - 1;

            for (int x = minX; x <= maxX; x++) {

                Entity one = itemService.getItem("tiled/fence", 1).build();
                Entity two = itemService.getItem("tiled/fence", 1).build();

                itemService.spawnItem(one, new GridPoint2(x, minY));
                itemService.spawnItem(two, new GridPoint2(x, maxY));

                foundationComponent.addToFences(one);
                foundationComponent.addToFences(two);
            }

            for (int y = minY; y <= maxY; y++) {

                Entity one = itemService.getItem("tiled/fence", 1).build();
                Entity two = itemService.getItem("tiled/fence", 1).build();

                itemService.spawnItem(one, new GridPoint2(minX, y));
                itemService.spawnItem(two, new GridPoint2(maxX, y));

                foundationComponent.addToFences(one);
                foundationComponent.addToFences(two);
            }
        }
        entity.remove(CreateFoundationFencesComponent.class);
    }
}
