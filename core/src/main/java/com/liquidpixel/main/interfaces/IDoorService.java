package com.liquidpixel.main.interfaces;

import com.badlogic.ashley.core.Entity;

public interface IDoorService {
    void enterDoor(Entity person);
    void closeDoor(Entity door);
    void openDoor(Entity door);
    void exitDoor(Entity person);
    boolean canExitDoor(Entity person);
    boolean canEnterDoor(Entity person);
    boolean hasExitedDoor(Entity person);
    boolean hasEnteredDoor(Entity person);
}
