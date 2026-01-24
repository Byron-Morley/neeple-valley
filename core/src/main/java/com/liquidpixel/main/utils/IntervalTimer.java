package com.liquidpixel.main.utils;

import com.badlogic.gdx.utils.TimeUtils;

public class IntervalTimer {
    private long lastUpdateTime = 0;
    private final long updateInterval;

    public IntervalTimer(long updateIntervalMs) {
        this.updateInterval = updateIntervalMs;
    }

    public boolean isReady() {
        long currentTime = TimeUtils.millis();
        if (currentTime - lastUpdateTime >= updateInterval) {
            lastUpdateTime = currentTime;
            return true;
        }
        return false;
    }
}
