package com.liquidpixel.main.interfaces;

import com.liquidpixel.main.interfaces.ScenarioState;

import java.util.List;

public interface IScenario {

    void start();
    void reset();

    /**
     * Get all available states for this scenario
     * @return List of scenario states, or empty list if no custom states are available
     */
    default List<ScenarioState> getAvailableStates() {
        return List.of();
    }

    /**
     * Load a specific state for this scenario
     * @param stateId The ID of the state to load
     * @return true if the state was loaded successfully, false if state ID is unknown
     */
    default boolean loadState(String stateId) {
        return false;
    }
}
