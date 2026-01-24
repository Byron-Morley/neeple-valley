package com.liquidpixel.main.interfaces;

public interface GameSetup {
    void initializeLogs();
    void initializeStage();
    void initializeFactories();
    void initializeServices();
    void initializeManagers();
    void initializeListeners();
    void initializeSystems();
    void initializeGame();
    void reset();
}
