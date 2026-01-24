package com.liquidpixel.main.services;

import com.liquidpixel.main.interfaces.managers.IPlayerInputManager;
import com.liquidpixel.main.interfaces.services.IPlayerInputService;

public class PlayerInputService extends Service implements IPlayerInputService {

    IPlayerInputManager playerInputManager;

    public PlayerInputService(IPlayerInputManager playerInputManager) {
        this.playerInputManager = playerInputManager;
    }

    @Override
    public boolean isLeftClickAllowed() {
        if (!playerInputManager.isPreviousLeftClicking()) {
            playerInputManager.setPreviousLeftClicking(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean isLeftClicking() {
        return playerInputManager.isLeftClicking();
    }

    @Override
    public boolean isRightClicking() {
        return playerInputManager.isRightClicking();
    }

    @Override
    public boolean isNewLeftClick() {
        return playerInputManager.isNewLeftClick();
    }

    @Override
    public boolean isNewRightClick() {
        return playerInputManager.isNewRightClick();
    }


}
