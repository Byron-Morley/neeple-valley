package com.liquidpixel.main.components;

import com.badlogic.ashley.core.Component;

public class AreaSpawnConfigurationComponent implements Component {

    boolean cancelled = false;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
