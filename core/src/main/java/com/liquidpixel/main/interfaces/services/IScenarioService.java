package com.liquidpixel.main.interfaces.services;

import com.liquidpixel.main.interfaces.IScenario;
import java.util.List;

public interface IScenarioService {

    List<String> getAvailableScenarios();

    IScenario loadScenario(String scenarioName);

    void startCurrentScenario();

    void resetCurrentScenario();

    void clearCurrentScenario();

    IScenario getCurrentScenario();

    boolean hasCurrentScenario();
}
