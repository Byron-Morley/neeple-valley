package com.liquidpixel.main.interfaces.managers;

import com.liquidpixel.main.services.PlayerInputService;

public interface IPlayerInputManager {
    PlayerInputService getPlayerInputService();

    boolean isPreviousLeftClicking();

    void setPreviousLeftClicking(boolean previousLeftClicking);

    boolean isLeftClicking();

    boolean isRightClicking();

    boolean isNewLeftClick();

    boolean isNewRightClick();
}
