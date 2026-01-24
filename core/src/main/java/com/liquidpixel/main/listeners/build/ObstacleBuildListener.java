package com.liquidpixel.main.listeners.build;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.ai.pathfinding.FlatTiledNode;
import com.liquidpixel.main.components.BodyComponent;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.model.sprite.CustomGridPoint;
import com.liquidpixel.main.model.sprite.NodeType;
import com.liquidpixel.main.utils.Mappers;

import java.util.List;

public class ObstacleBuildListener implements EntityListener {

    IWorldMap worldMap;

    public ObstacleBuildListener(IWorldMap worldMap) {
        this.worldMap = worldMap;
    }

    @Override
    public void entityAdded(Entity entity) {
        addBodyPositions(entity);
    }

    @Override
    public void entityRemoved(Entity entity) {
        removeBodyPositions(entity);
    }

    public void addBodyPositions(Entity entity) {
        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
        GridPoint2 position = positionComponent.getGridPosition();
        positionComponent.setPreviousPosition(new GridPoint2(position));
        BodyComponent bodyComponent = entity.getComponent(BodyComponent.class);

        List<CustomGridPoint> positions = bodyComponent.getAllAbsolutePositions(position);

        for (CustomGridPoint point : positions) {
            FlatTiledNode currentNode = worldMap.getNode(new GridPoint2(point));

            if(currentNode!=null) {
                if (point.getN().getValue() > currentNode.getType().getValue()) {
                    worldMap.updateNodeType(new GridPoint2(point), point.getN());
                }
                worldMap.addEntity(entity, new GridPoint2(point));
            }else{
                System.out.println("warning, Entity not added to world map");
            }
        }

        GridPoint2 adjustedPosition = new GridPoint2(position).add(new GridPoint2(bodyComponent.getInteractionPoint()));
        bodyComponent.setInteractionPoint(adjustedPosition);
    }

    public void removeBodyPositions(Entity entity) {
        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
        GridPoint2 position = positionComponent.getPreviousPosition();
        BodyComponent bodyComponent = Mappers.body.get(entity);
        List<CustomGridPoint> positions = bodyComponent.getAllAbsolutePositions(position);

        for (CustomGridPoint point : positions) {
            worldMap.removeEntity(entity, new GridPoint2(point));

            List<Entity> remainingEntities = worldMap.getEntitiesAtPosition(new GridPoint2(point));

            if (remainingEntities.isEmpty()) {
                worldMap.updateNodeType(new GridPoint2(point), NodeType.TILE_FLOOR);
                worldMap.refreshConnections(new GridPoint2(point));
            } else {
                NodeType highestType = NodeType.TILE_FLOOR; // Default

                for (Entity e : remainingEntities) {
                    BodyComponent eBody = Mappers.body.get(e);
                    if(eBody != null) {
                        List<CustomGridPoint> ePositions = eBody.getAllAbsolutePositions(
                            Mappers.position.get(e).getGridPosition());

                        for (CustomGridPoint ePoint : ePositions) {
                            if (ePoint.x == point.x && ePoint.y == point.y) {
                                if (ePoint.getN().getValue() > highestType.getValue()) {
                                    highestType = ePoint.getN();
                                }
                                break;
                            }
                        }
                    }
                }
                worldMap.updateNodeType(new GridPoint2(point), highestType);
            }
        }
    }
}
