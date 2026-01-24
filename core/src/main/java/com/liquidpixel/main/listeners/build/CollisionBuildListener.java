package com.liquidpixel.main.listeners.build;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.render.RenderComponent;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.utils.Mappers;

public class CollisionBuildListener implements EntityListener {

    IWorldMap worldMap;

    public CollisionBuildListener(IWorldMap worldMap) {
        this.worldMap = worldMap;
    }

    @Override
    public void entityAdded(Entity entity) {

//          if (Mappers.position.has(entity)) {
//              GridPoint2 position = Mappers.position.get(entity).getGridPosition();
//              Mappers.position.get(entity).setPreviousPosition(new GridPoint2(position));
//              worldMap.addEntity(entity, new GridPoint2(position));
//              System.out.println("CollisionBuildListener.entityAdded");

//              if (Mappers.collision.has(entity)) {
//                  RenderComponent renderComponent = Mappers.render.get(entity);
//                  worldMap.addObstacle(new GridPoint2(position), renderComponent.getWidth(), renderComponent.getHeight());
//              }

//              if(Mappers.agent.has(entity)){
//                  System.out.println("CollisionBuildListener.entityAdded: agent");
//                  RenderComponent renderComponent = Mappers.render.get(entity);
////                  worldMap.addObstacle(new GridPoint2(position), renderComponent.getWidth(), renderComponent.getHeight());
//              }
//          }
    }

    @Override
    public void entityRemoved(Entity entity) {

//        if (Mappers.position.has(entity)) {
//            GridPoint2 position = Mappers.position.get(entity).getGridPosition();
//            worldMap.removeEntity(entity, new GridPoint2(position));
//            System.out.println("CollisionBuildListener.entityRemoved");
//
//            if (Mappers.collision.has(entity)) {
//                RenderComponent renderComponent = Mappers.render.get(entity);
//                worldMap.removeObstacle(new GridPoint2(position), renderComponent.getWidth(), renderComponent.getHeight());
//            }
//        }
    }
}
