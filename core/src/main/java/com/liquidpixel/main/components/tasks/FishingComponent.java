package com.liquidpixel.main.components.tasks;

import com.badlogic.ashley.core.Component;

public class FishingComponent implements Component {
    public enum State {
        IDLE,
        LOCATING_TILE,
        MOVING,
        FISHING
    }

    public FishingComponent.State state = FishingComponent.State.IDLE;




}
