package com.liquidpixel.main.services.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.liquidpixel.main.model.config.KeyBindings;
import com.liquidpixel.main.model.config.SystemAction;
import com.liquidpixel.main.model.player.PlayerAction;
import com.liquidpixel.core.core.Direction;

public class KeyBindingService {
    private static final String PREFS_NAME = "node_trader_keybindings";

    public static void saveKeyBindings(KeyBindings keyBindings) {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);

        // Save movement bindings
        for (Direction dir : Direction.values()) {
            prefs.putInteger("MOVEMENT_" + dir.name(), keyBindings.getMovementKey(dir));
        }

        // Save action bindings
        for (PlayerAction action : PlayerAction.values()) {
            prefs.putInteger("ACTION_" + action.name(), keyBindings.getActionKey(action));
        }

        // Save system bindings
        prefs.putInteger("SYSTEM_TOGGLE_CONNECTIONS", keyBindings.getSystemKey("TOGGLE_CONNECTIONS"));
        prefs.putInteger("SYSTEM_TOGGLE_PAUSE", keyBindings.getSystemKey("TOGGLE_PAUSE"));
        prefs.putInteger("SYSTEM_SPEED_NORMAL", keyBindings.getSystemKey("SPEED_NORMAL"));
        prefs.putInteger("SYSTEM_SPEED_FAST", keyBindings.getSystemKey("SPEED_FAST"));
        prefs.putInteger("SYSTEM_SPEED_FASTER", keyBindings.getSystemKey("SPEED_FASTER"));

        prefs.flush();
    }

    public static KeyBindings loadKeyBindings() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        KeyBindings keyBindings = new KeyBindings(); // Start with defaults

        // If we have saved preferences, load them
        if (prefs.contains("MOVEMENT_UP")) {
            // Load movement bindings
            for (Direction dir : Direction.values()) {
                int key = prefs.getInteger("MOVEMENT_" + dir.name(), keyBindings.getMovementKey(dir));
                keyBindings.setMovementKey(dir, key);
            }

            // Load action bindings
            for (PlayerAction action : PlayerAction.values()) {
                int key = prefs.getInteger("ACTION_" + action.name(), keyBindings.getActionKey(action));
                keyBindings.setActionKey(action, key);
            }

            // Load system bindings
            keyBindings.setSystemKey(SystemAction.TOGGLE_CONNECTIONS,
                prefs.getInteger("SYSTEM_TOGGLE_CONNECTIONS", keyBindings.getSystemKey("TOGGLE_CONNECTIONS")));
            keyBindings.setSystemKey(SystemAction.TOGGLE_PAUSE,
                prefs.getInteger("SYSTEM_TOGGLE_PAUSE", keyBindings.getSystemKey("TOGGLE_PAUSE")));
            keyBindings.setSystemKey(SystemAction.SPEED_NORMAL,
                prefs.getInteger("SYSTEM_SPEED_NORMAL", keyBindings.getSystemKey("SPEED_NORMAL")));
            keyBindings.setSystemKey(SystemAction.SPEED_FAST,
                prefs.getInteger("SYSTEM_SPEED_FAST", keyBindings.getSystemKey("SPEED_FAST")));
            keyBindings.setSystemKey(SystemAction.SPEED_FASTER,
                prefs.getInteger("SYSTEM_SPEED_FASTER", keyBindings.getSystemKey("SPEED_FASTER")));
        }

        return keyBindings;
    }
}
