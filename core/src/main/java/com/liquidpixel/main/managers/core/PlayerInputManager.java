package com.liquidpixel.main.managers.core;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.liquidpixel.main.engine.GameState;
import com.liquidpixel.main.interfaces.managers.IPlayerInputManager;
import com.liquidpixel.main.interfaces.ui.IUIService;
import com.liquidpixel.main.model.config.KeyBindings;
import com.liquidpixel.main.model.config.SystemAction;
import com.liquidpixel.main.model.player.PlayerAction;
import com.liquidpixel.core.core.Direction;
import com.liquidpixel.main.services.PlayerInputService;
import com.liquidpixel.main.utils.events.Messages;

import java.util.LinkedList;
import java.util.Stack;

import static com.liquidpixel.main.utils.events.Messages.TOGGLE_CONNECTIONS;
import static com.liquidpixel.main.utils.events.Messages.TOGGLE_PAUSE_MENU;

public class PlayerInputManager extends ClickListener implements IPlayerInputManager {
    private Stack<Direction> movementKeysPressed;
    private Stack<PlayerAction> actionKeyPressed;

    private boolean previousLeftClicking;
    private boolean isLeftClicking;
    private boolean isRightClicking;

    private boolean isNewLeftClick;
    private boolean isNewRightClick;


    private LinkedList<Float> scrollQueue;
    private KeyBindings keyBindings;
    IUIService uiService;
    PlayerInputService playerInputService;

    public PlayerInputManager() {
        isLeftClicking = false;
        isRightClicking = false;
        previousLeftClicking = false;
        this.movementKeysPressed = new Stack<>();
        this.actionKeyPressed = new Stack<>();
        this.scrollQueue = new LinkedList<>();
        this.setButton(-1);
        this.keyBindings = new KeyBindings();
        playerInputService = new PlayerInputService(this);
    }

    public Direction getPlayersNewDirection() {
        int keysPressed = movementKeysPressed.size();

        if (keysPressed == 0)
            return null;
        else
            return movementKeysPressed.peek();
    }

    private Direction keycodeToDirection(int keycode) {
        return keyBindings.getDirectionForKey(keycode);
    }

    public boolean isLeftClicking() {
        return this.isLeftClicking;
    }

    public boolean isRightClicking() {
        return this.isRightClicking;
    }

    private void handleMovementKeys(int keycode) {
        Direction direction = keycodeToDirection(keycode);

        if (direction != null && !movementKeysPressed.contains(direction)) {
            movementKeysPressed.push(direction);
        }
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        handleMovementKeys(keycode);
        handleActionKeys(keycode);
        return super.keyDown(event, keycode);
    }

    private void handleActionKeys(int keycode) {
        PlayerAction action = keyBindings.getActionForKey(keycode);
        if (action != null) {
            actionKeyPressed.push(action);
        }

        SystemAction systemAction = keyBindings.getSystemActionForKey(keycode);
        if (systemAction != null) {
            switch (systemAction) {
                case TOGGLE_CONNECTIONS:
                    MessageManager.getInstance().dispatchMessage(TOGGLE_CONNECTIONS);
                    break;
                case TOGGLE_PAUSE:
                    MessageManager.getInstance().dispatchMessage(TOGGLE_PAUSE_MENU);
                    break;
                case SPEED_NORMAL:
                    GameState.setTimeScale(1.0f);
                    break;
                case SPEED_FAST:
                    GameState.setTimeScale(3.0f);
                    break;
                case SPEED_FASTER:
                    GameState.setTimeScale(5.0f);
                    break;
                case TOGGLE_WORK_ORDERS:
                    MessageManager.getInstance().dispatchMessage(Messages.TOGGLE_WORK_ORDERS);
                    break;
                case TOGGLE_PEOPLE_UI:
                    MessageManager.getInstance().dispatchMessage(Messages.TOGGLE_PEOPLE_UI);
                    break;
            }
        }
    }


    @Override
    public boolean keyUp(InputEvent event, int keycode) {
        Direction direction = keycodeToDirection(keycode);
        if (direction != null) {
            movementKeysPressed.remove(direction);
        }

        PlayerAction action = keyBindings.getActionForKey(keycode);
        if (action != null) {
            actionKeyPressed.remove(action);
        }

        return super.keyUp(event, keycode);
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            this.isLeftClicking = true;
            this.isNewLeftClick = true;
        }
        if (button == Input.Buttons.RIGHT) {
            this.isRightClicking = true;
            this.isNewRightClick = true;
        }
        return super.touchDown(event, x, y, pointer, button);
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            this.isLeftClicking = false;
            this.previousLeftClicking = false;
        }
        if (button == Input.Buttons.RIGHT) {
            this.isRightClicking = false;
        }
        super.touchUp(event, x, y, pointer, button);
    }

    @Override
    public boolean isNewLeftClick() {
        boolean result = isNewLeftClick;
        isNewLeftClick = false;
        return result;
    }

    @Override
    public boolean isNewRightClick() {
        boolean result = isNewRightClick;
        isNewRightClick = false;
        return result;
    }

    public Stack<Direction> getMovementKeysPressed() {
        return movementKeysPressed;
    }

    @Override
    public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
        if (uiService.isMouseOverUI()) {
            return super.scrolled(event, x, y, amountX, amountY);
        } else {
            scrollQueue.add(amountY);
            return true;
        }
    }

    public LinkedList<Float> getScrollQueue() {
        return scrollQueue;
    }

    public Stack<PlayerAction> getActionKeyPressed() {
        return actionKeyPressed;
    }

    public boolean isPreviousLeftClicking() {
        return previousLeftClicking;
    }

    public void setPreviousLeftClicking(boolean previousLeftClicking) {
        this.previousLeftClicking = previousLeftClicking;
    }

    public PlayerInputService getPlayerInputService() {
        return playerInputService;
    }

    public void setUiService(IUIService uiService) {
        this.uiService = uiService;
    }

    public KeyBindings getKeyBindings() {
        return keyBindings;
    }

    public void setKeyBindings(KeyBindings keyBindings) {
        this.keyBindings = keyBindings;
    }

    public void saveKeyBindings() {
    }

    public void loadKeyBindings() {
    }
}
