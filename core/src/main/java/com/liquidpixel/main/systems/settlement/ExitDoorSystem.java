package com.liquidpixel.main.systems.settlement;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.liquidpixel.core.core.Status;
import com.liquidpixel.main.components.DoNotRenderComponent;
import com.liquidpixel.main.components.DoorComponent;
import com.liquidpixel.main.components.colony.ExitDoorComponent;
import com.liquidpixel.main.components.render.FadeComponent;
import com.liquidpixel.main.interfaces.IDoorService;
import com.liquidpixel.main.interfaces.IRenderService;
import com.liquidpixel.main.managers.EntityInteractionManager;
import com.liquidpixel.core.core.Action;
import com.liquidpixel.main.services.DoorService;
import com.liquidpixel.main.services.RenderService;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.sprite.api.services.IAnimationService;
import com.liquidpixel.sprite.services.AnimationService;

public class ExitDoorSystem extends IteratingSystem {

    IDoorService doorService;
    IRenderService renderService;

    public ExitDoorSystem() {
        super(Family.all(ExitDoorComponent.class).get());
        doorService = new DoorService();
        renderService = new RenderService();
    }

    @Override
    protected void processEntity(Entity person, float deltaTime) {
        ExitDoorComponent exitDoor = Mappers.exitDoor.get(person);
        switch (exitDoor.state) {
            case CLOSED:
                handleClosedState(person, exitDoor);
                break;
            case OPENING:
                handleOpeningState(person, exitDoor);
                break;
            case WALKING:
                handleWalkingState(person, exitDoor);
                break;
            case OPEN:
                handleOpenState(person, exitDoor);
                break;
        }
    }

    private void handleOpenState(Entity person, ExitDoorComponent exitDoor) {
        Entity door = exitDoor.getDoor();
        IAnimationService animationService = doorService.closeDoor(door);
        animationService.addListener(new AnimationService.AnimationListener() {
            @Override
            public void onAnimationFinished() {
                animationService.setAnimation(new Status("CLOSED", ""));
            }
        });
        DoorComponent doorComponent = Mappers.door.get(door);
        doorComponent.removeOccupant(person);
        person.remove(ExitDoorComponent.class);
        Mappers.status.get(person).setAction(Action.STANDING);
    }

    private void handleWalkingState(Entity person, ExitDoorComponent exitDoor) {

        if (Mappers.fade.has(person)) {
            FadeComponent fadeComponent = Mappers.fade.get(person);
            if (!fadeComponent.isFading() && !renderService.isFullyVisible(person)) {
                renderService.fadeIn(person, 0.5f);
            }
        } else {
            if (!renderService.isFullyVisible(person)) {
                renderService.fadeIn(person, 0.5f);
            }
        }
        doorService.exitDoor(person);
    }

    private void handleOpeningState(Entity person, ExitDoorComponent exitDoor) {
        IAnimationService animationService = new AnimationService(exitDoor.getDoor());
        person.remove(DoNotRenderComponent.class);
        animationService.addListener(new AnimationService.AnimationListener() {
            @Override
            public void onAnimationFinished() {
                exitDoor.state = Action.WALKING;
            }
        });
    }

    private void handleClosedState(Entity person, ExitDoorComponent exitDoor) {
        if (doorService.canExitDoor(person)) {
            EntityInteractionManager.getInstance().registerInteraction(exitDoor.getDoor(), person);

            exitDoor.state = Action.OPENING;
        } else {
            person.remove(ExitDoorComponent.class);
        }
    }
}
