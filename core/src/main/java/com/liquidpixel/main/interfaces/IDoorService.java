package com.liquidpixel.main.interfaces;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.sprite.api.services.IAnimationService;

public interface IDoorService {
    void enterDoor(Entity person);
    IAnimationService closeDoor(Entity door);
    IAnimationService openDoor(Entity door);
    void exitDoor(Entity person);
    boolean canExitDoor(Entity person);
    boolean canEnterDoor(Entity person);
    boolean hasExitedDoor(Entity person);
    boolean hasEnteredDoor(Entity person);

}
