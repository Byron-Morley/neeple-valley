package com.liquidpixel.main.interfaces.managers;

public interface ILoadAndSaveManager {
    void saveGame(String saveName, Runnable onComplete);
    void loadGame(String saveName, Runnable onComplete);
    void newGame(Runnable onComplete);
}
