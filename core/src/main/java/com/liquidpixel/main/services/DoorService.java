package com.liquidpixel.main.services;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.core.components.core.StatusComponent;
import com.liquidpixel.core.core.Status;
import com.liquidpixel.main.components.DoNotRenderComponent;
import com.liquidpixel.main.components.DoorComponent;
import com.liquidpixel.pathfinding.components.TraverseComponent;
import com.liquidpixel.main.components.colony.EnterDoorComponent;
import com.liquidpixel.main.components.colony.ExitDoorComponent;
import com.liquidpixel.main.interfaces.IDoorService;
import com.liquidpixel.core.core.Action;
import com.liquidpixel.core.core.Direction;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.sprite.api.services.IAnimationService;
import com.liquidpixel.sprite.services.AnimationService;

public class DoorService extends Service implements IDoorService {

    @Override
    public IAnimationService openDoor(Entity door) {
        IAnimationService animationService = new AnimationService(door);
        animationService.setAnimation(new Status("OPENING", ""));
        return animationService;
    }

    @Override
    public IAnimationService closeDoor(Entity door) {
        IAnimationService animationService = new AnimationService(door);
        animationService.setAnimation(new Status("CLOSING", ""));
        return animationService;
    }

    @Override
    public void enterDoor(Entity person) {
        EnterDoorComponent enterDoor = Mappers.enterDoor.get(person);
        GridPoint2 origin = enterDoor.getInside();
        if (Mappers.position.get(person).getPosition().equals(new Vector2(origin.x, origin.y))) {
            enterDoor.state = EnterDoorComponent.State.CLOSE;
        } else if (!Mappers.traverse.has(person)) {
            walkInDoors(person);
        }

        if (Mappers.traverse.has(person)) {
            TraverseComponent traverse = Mappers.traverse.get(person);
            if (!traverse.getDestination().equals(enterDoor.getInside())){
                walkInDoors(person);
            }
        }
    }

    private void walkInDoors(Entity person) {
        EnterDoorComponent enterDoor = Mappers.enterDoor.get(person);
        Mappers.status.get(person).setDirection(Direction.UP);
        TraverseComponent traverse = new TraverseComponent(enterDoor.getInside());
        traverse.getWaypoints().add(enterDoor.getInside());
        person.add(traverse);
    }

    @Override
    public void exitDoor(Entity person) {
        ExitDoorComponent exitDoor = Mappers.exitDoor.get(person);
        GridPoint2 destination = exitDoor.getDoorStep();
        if (Mappers.position.get(person).getPosition().equals(new Vector2(destination.x, destination.y))) {
            exitDoor.state = Action.OPEN;
        } else if (!Mappers.traverse.has(person)) {
            Mappers.status.get(person).setDirection(Direction.DOWN);
            TraverseComponent traverse = new TraverseComponent(destination);
            traverse.getWaypoints().add(destination);
            person.add(traverse);
        }
    }

    @Override
    public boolean canExitDoor(Entity person) {
        ExitDoorComponent exitDoor = Mappers.exitDoor.get(person);
        Entity door = exitDoor.getDoor();

        DoorComponent doorComponent = Mappers.door.get(door);
        StatusComponent statusComponent = Mappers.status.get(door);
        RenderService renderService = new RenderService();

        if (doorComponent.isEntityInside(person)) {

            // MAKE INVISIBLE
            if (renderService.isFullyVisible(person)) {
                renderService.makeInvisible(person);
            }

            // SET POSITION
            if (Mappers.position.has(person)) {
                PositionComponent positionComponent = Mappers.position.get(person);
                positionComponent.setPosition(exitDoor.getInside());
            }

            // OPEN DOOR
            if (statusComponent.getAction() == Action.CLOSED) {
                IAnimationService animationService = openDoor(door);
                animationService.addListener(new AnimationService.AnimationListener() {
                    @Override
                    public void onAnimationFinished() {
                        animationService.setAnimation(new Status("OPEN", ""));
                    }
                });
            }
            return true;
        } else {
            return false;
        }
    }


    @Override
    public boolean hasExitedDoor(Entity person) {
        return false;
    }

    @Override
    public boolean canEnterDoor(Entity person) {
        EnterDoorComponent enterDoor = Mappers.enterDoor.get(person);
        Entity door = enterDoor.getDoor();
        DoorComponent doorComponent = Mappers.door.get(door);
        StatusComponent statusComponent = Mappers.status.get(door);

        if (!doorComponent.isEntityInside(person)) {

            if (Mappers.position.has(person)) {
                PositionComponent personPosition = Mappers.position.get(person);

                // IS PERSON AT THE DOOR?
                if (personPosition.getGridPosition().equals(enterDoor.getDoorStep())) {

                    //OPEN DOOR
                    if (statusComponent.getAction() == Action.CLOSED) {
                        openDoor(door);
                    }

                } else {
                    return false;
                }
            }

            return true;
        } else {
            person.add(new DoNotRenderComponent());
            return false;
        }
    }

    @Override
    public boolean hasEnteredDoor(Entity person) {
        EnterDoorComponent enterDoor = Mappers.enterDoor.get(person);
        Entity door = enterDoor.getDoor();
        DoorComponent doorComponent = Mappers.door.get(door);
        return doorComponent.isEntityInside(person);
    }
}
