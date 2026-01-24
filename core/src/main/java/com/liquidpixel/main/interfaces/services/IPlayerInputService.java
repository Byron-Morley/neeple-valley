package com.liquidpixel.main.interfaces.services;

public interface IPlayerInputService {
    boolean isLeftClickAllowed();

    boolean isLeftClicking();

    boolean isRightClicking();

    boolean isNewLeftClick();

    boolean isNewRightClick();
}
