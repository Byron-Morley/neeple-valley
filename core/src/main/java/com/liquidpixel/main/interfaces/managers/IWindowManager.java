package com.liquidpixel.main.interfaces.managers;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.liquidpixel.main.interfaces.IGet;

public interface IWindowManager {
    void registerWindow(int id, IGet<Group> window);
    IGet<Group> getWindow(int id);
}
