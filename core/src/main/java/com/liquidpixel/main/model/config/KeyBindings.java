package com.liquidpixel.main.model.config;

import com.badlogic.gdx.Input;
import com.liquidpixel.main.model.player.PlayerAction;
import com.liquidpixel.core.core.Direction;
import java.util.HashMap;
import java.util.Map;

public class KeyBindings {
    private Map<Direction, Integer> movementBindings;
    private Map<PlayerAction, Integer> actionBindings;
    private Map<SystemAction, Integer> systemBindings;

    public KeyBindings() {
        // Initialize with default bindings
        movementBindings = new HashMap<>();
        movementBindings.put(Direction.UP, Input.Keys.W);
        movementBindings.put(Direction.DOWN, Input.Keys.S);
        movementBindings.put(Direction.LEFT, Input.Keys.A);
        movementBindings.put(Direction.RIGHT, Input.Keys.D);

        actionBindings = new HashMap<>();
        actionBindings.put(PlayerAction.ACTION, Input.Keys.SPACE);

        systemBindings = new HashMap<>();
        systemBindings.put(SystemAction.TOGGLE_CONNECTIONS, Input.Keys.ALT_LEFT);
        systemBindings.put(SystemAction.TOGGLE_PAUSE, Input.Keys.ESCAPE);
        systemBindings.put(SystemAction.SPEED_NORMAL, Input.Keys.NUM_1);
        systemBindings.put(SystemAction.SPEED_FAST, Input.Keys.NUM_2);
        systemBindings.put(SystemAction.SPEED_FASTER, Input.Keys.NUM_3);
        systemBindings.put(SystemAction.TOGGLE_WORK_ORDERS, Input.Keys.O);
        systemBindings.put(SystemAction.TOGGLE_PEOPLE_UI, Input.Keys.P);
    }

    public int getMovementKey(Direction direction) {
        return movementBindings.get(direction);
    }

    public int getActionKey(PlayerAction action) {
        return actionBindings.get(action);
    }

    public int getSystemKey(String action) {
        return systemBindings.get(action);
    }

    public void setMovementKey(Direction direction, int keycode) {
        movementBindings.put(direction, keycode);
    }

    public void setActionKey(PlayerAction action, int keycode) {
        actionBindings.put(action, keycode);
    }

    public void setSystemKey(SystemAction action, int keycode) {
        systemBindings.put(action, keycode);
    }

    public Direction getDirectionForKey(int keycode) {
        for (Map.Entry<Direction, Integer> entry : movementBindings.entrySet()) {
            if (entry.getValue() == keycode) {
                return entry.getKey();
            }
        }
        return null;
    }

    public PlayerAction getActionForKey(int keycode) {
        for (Map.Entry<PlayerAction, Integer> entry : actionBindings.entrySet()) {
            if (entry.getValue() == keycode) {
                return entry.getKey();
            }
        }
        return null;
    }

    public SystemAction getSystemActionForKey(int keycode) {
        for (Map.Entry<SystemAction, Integer> entry : systemBindings.entrySet()) {
            if (entry.getValue() == keycode) {
                return entry.getKey();
            }
        }
        return null;
    }
}
