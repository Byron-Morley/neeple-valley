package com.liquidpixel.main.systems.settlement;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.liquidpixel.main.components.DoNotRenderComponent;
import com.liquidpixel.main.components.DoorComponent;
import com.liquidpixel.pathfinding.components.MovementTaskComponent;
import com.liquidpixel.main.components.colony.EnterDoorComponent;
import com.liquidpixel.main.components.render.FadeComponent;
import com.liquidpixel.main.interfaces.IDoorService;
import com.liquidpixel.main.interfaces.IRenderService;
import com.liquidpixel.main.services.DoorService;
import com.liquidpixel.main.services.RenderService;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.sprite.api.services.IAnimationService;
import com.liquidpixel.sprite.services.AnimationService;

public class EnterDoorSystem extends IteratingSystem {

    IDoorService doorService;
    IRenderService renderService;

    public EnterDoorSystem() {
        super(Family.all(EnterDoorComponent.class).get());
        doorService = new DoorService();
        renderService = new RenderService();
    }

    @Override
    protected void processEntity(Entity person, float deltaTime) {
        EnterDoorComponent enterDoor = Mappers.enterDoor.get(person);
        switch (enterDoor.state) {
            case IDLE:
                handleIdleState(person);
                break;
            case OPENING:
                handleOpeningState(person);
                break;
            case WALKING:
                handleStartWalkingState(person);
                break;
            case CLOSE:
                handleClosedState(person);
                break;
        }
    }

    private void handleStartWalkingState(Entity person) {
        if (Mappers.fade.has(person)) {
            FadeComponent fadeComponent = Mappers.fade.get(person);
            if (!fadeComponent.isFading() && renderService.isFullyVisible(person)) {
                renderService.fadeOut(person, 0.5f);
            }
        } else {
            if (renderService.isFullyVisible(person)) {
                renderService.fadeOut(person, 0.5f);
            }
        }
        doorService.enterDoor(person);
    }

    private void handleOpeningState(Entity person) {
        EnterDoorComponent enterDoor = Mappers.enterDoor.get(person);
        IAnimationService animationService = new AnimationService(enterDoor.getDoor());
        if (animationService.isAnimationFinished()) {
            enterDoor.state = EnterDoorComponent.State.WALKING;
        }
    }

    private void handleIdleState(Entity person) {

        EnterDoorComponent enterDoor = Mappers.enterDoor.get(person);
        if (doorService.canEnterDoor(person)) {
            enterDoor.state = EnterDoorComponent.State.OPENING;
            System.out.println("entering door");
        } else if(!Mappers.movetask.has(person)){
            person.add(new MovementTaskComponent(enterDoor.getDoorStep()));
        }

        if (Mappers.movetask.has(person) && Mappers.movetask.get(person).state == MovementTaskComponent.State.FAILED) {
            person.remove(EnterDoorComponent.class);
        }
    }

    private void handleClosedState(Entity person) {
        EnterDoorComponent enterDoor = Mappers.enterDoor.get(person);
        Entity door = enterDoor.getDoor();
        DoorComponent doorComponent = Mappers.door.get(door);
        doorComponent.addOccupant(person);
        doorService.closeDoor(door);
        person.remove(EnterDoorComponent.class);
        person.add(new DoNotRenderComponent());
    }
}
