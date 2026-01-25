package com.liquidpixel.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.liquidpixel.main.tools.ScenarioBuilderApplication;

public class LaunchScenarioEditor {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("DEBUG: entered LaunchScenarioEditor.main()");

        // Pause so debugger has time to attach
        Thread.sleep(2_000);

        // Skip JVM restart if debugger is attached
        if (!isDebuggerAttached() && StartupHelper.startNewJvmIfRequired()) {
            return; // original behavior
        }

        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new ScenarioBuilderApplication(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("neeple valley");
        configuration.useVsync(true);
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        configuration.setWindowedMode(1920, 1080);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }

    /** Returns true if the JVM is currently being debugged via JDWP */
    private static boolean isDebuggerAttached() {
        String inputArgs = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString();
        return inputArgs.contains("-agentlib:jdwp");
    }
}
