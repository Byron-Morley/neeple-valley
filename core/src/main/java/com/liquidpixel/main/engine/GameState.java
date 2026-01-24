package com.liquidpixel.main.engine;

import com.liquidpixel.main.model.terrain.BaseTerrain;

public class GameState {

    private static boolean isPaused = false;
    private static float timeScale = 1.0f;
    private static BaseTerrain currentTerrain = BaseTerrain.SUMMER;
    private static long seed = 0;


    public static void setTimeScale(float scale) {
        timeScale = scale;
    }

    public static float getTimeScale() {
        return timeScale;
    }

    public static void togglePause() {
        isPaused = !isPaused;
        System.out.println("isPaused: " + isPaused);
    }

    public static void setPaused(boolean paused) {
        isPaused = paused;
    }

    public static boolean isPaused() {
        return isPaused;
    }

    public static BaseTerrain getCurrentTerrain() {
        return currentTerrain;
    }

    public static String getCurrentTerrainName() {
        return currentTerrain.name();
    }

    public static String getCurrentTerrainId() {
        return currentTerrain.getId();
    }

    public static void setCurrentTerrain(BaseTerrain currentTerrain) {
        GameState.currentTerrain = currentTerrain;
    }

    public static long getSeed() {
        return seed;
    }

    public static void setSeed(long seed) {
        GameState.seed = seed;
    }
}
